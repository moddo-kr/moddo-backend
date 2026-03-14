package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

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
}
