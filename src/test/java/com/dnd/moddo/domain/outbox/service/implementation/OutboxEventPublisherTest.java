package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.impl.OutboxEventPublisher;
import com.dnd.moddo.outbox.application.impl.OutboxEventTaskAppender;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@Mock
	private OutboxEventTaskAppender outboxEventTaskAppender;

	@InjectMocks
	private OutboxEventPublisher outboxEventPublisher;

	@Test
	@DisplayName("PENDING 아웃박스 이벤트를 publish하면 태스크를 추가하고 published 상태로 변경한다.")
	void publishPendingOutboxEvent() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxEventRepository.getById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getStatus()).thenReturn(OutboxEventStatus.PENDING);

		outboxEventPublisher.publish(1L);

		verify(outboxEventTaskAppender).appendTasks(outboxEvent);
		verify(outboxEvent).markPublished();
		verify(outboxEvent, never()).markFailed();
	}

	@Test
	@DisplayName("태스크 추가 중 예외가 발생하면 failed 상태로 변경한다.")
	void markFailedWhenAppendTaskThrowsException() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxEventRepository.getById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getStatus()).thenReturn(OutboxEventStatus.PENDING);
		doThrow(new RuntimeException("append failed")).when(outboxEventTaskAppender).appendTasks(outboxEvent);

		outboxEventPublisher.publish(1L);

		verify(outboxEvent).markFailed();
		verify(outboxEvent, never()).markPublished();
	}

	@Test
	@DisplayName("pending 아웃박스 이벤트 목록을 순서대로 publish한다.")
	void publishPendingEvents() {
		OutboxEvent first = mock(OutboxEvent.class);
		OutboxEvent second = mock(OutboxEvent.class);
		when(first.getId()).thenReturn(1L);
		when(second.getId()).thenReturn(2L);
		when(outboxEventRepository.findAllByStatus(OutboxEventStatus.PENDING)).thenReturn(List.of(first, second));
		when(outboxEventRepository.getById(1L)).thenReturn(first);
		when(outboxEventRepository.getById(2L)).thenReturn(second);
		when(first.getStatus()).thenReturn(OutboxEventStatus.PENDING);
		when(second.getStatus()).thenReturn(OutboxEventStatus.PENDING);

		outboxEventPublisher.publishPendingEvents();

		verify(outboxEventTaskAppender).appendTasks(first);
		verify(outboxEventTaskAppender).appendTasks(second);
	}

	@Test
	@DisplayName("pending 상태가 아니면 publish를 건너뛴다.")
	void skipWhenOutboxEventAlreadyProcessed() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxEventRepository.getById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getStatus()).thenReturn(OutboxEventStatus.PUBLISHED);

		outboxEventPublisher.publish(1L);

		verifyNoInteractions(outboxEventTaskAppender);
		verify(outboxEvent, never()).markPublished();
		verify(outboxEvent, never()).markFailed();
	}
}
