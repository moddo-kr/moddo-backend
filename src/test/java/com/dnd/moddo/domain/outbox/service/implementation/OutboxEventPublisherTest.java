package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.impl.OutboxReader;
import com.dnd.moddo.outbox.application.impl.OutboxEventPublishExecutor;
import com.dnd.moddo.outbox.application.impl.OutboxEventPublisher;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

	@Mock
	private OutboxReader outboxReader;

	@Mock
	private OutboxEventPublishExecutor outboxEventPublishExecutor;

	@InjectMocks
	private OutboxEventPublisher outboxEventPublisher;

	@Test
	@DisplayName("PENDING 아웃박스 이벤트를 publish하면 태스크를 추가하고 published 상태로 변경한다.")
	void publishPendingOutboxEvent() {
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(true);

		outboxEventPublisher.publish(1L);

		verify(outboxEventPublishExecutor).appendTasks(1L);
		verify(outboxEventPublishExecutor).markPublished(1L);
		verify(outboxEventPublishExecutor, never()).markFailed(1L);
	}

	@Test
	@DisplayName("태스크 추가 중 예외가 발생하면 failed 상태로 변경한다.")
	void markFailedWhenAppendTaskThrowsException() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(true);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getEventType()).thenReturn(OutboxEventType.SETTLEMENT_COMPLETED);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		doThrow(new RuntimeException("append failed")).when(outboxEventPublishExecutor).appendTasks(1L);

		outboxEventPublisher.publish(1L);

		verify(outboxEventPublishExecutor).markFailed(1L);
		verify(outboxEventPublishExecutor, never()).markPublished(1L);
	}

	@Test
	@DisplayName("pending 아웃박스 이벤트 목록을 순서대로 publish한다.")
	void publishPendingEvents() {
		OutboxEvent first = mock(OutboxEvent.class);
		OutboxEvent second = mock(OutboxEvent.class);
		when(first.getId()).thenReturn(1L);
		when(second.getId()).thenReturn(2L);
		when(outboxReader.findAllByStatus(OutboxEventStatus.PENDING)).thenReturn(List.of(first, second));
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(true);
		when(outboxEventPublishExecutor.claimProcessing(2L)).thenReturn(true);

		outboxEventPublisher.publishPendingEvents();

		verify(outboxEventPublishExecutor).appendTasks(1L);
		verify(outboxEventPublishExecutor).markPublished(1L);
		verify(outboxEventPublishExecutor).appendTasks(2L);
		verify(outboxEventPublishExecutor).markPublished(2L);
	}

	@Test
	@DisplayName("이미 다른 트랜잭션이 선점했으면 publish를 건너뛴다.")
	void skipWhenOutboxEventAlreadyProcessed() {
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(false);

		outboxEventPublisher.publish(1L);

		verifyNoInteractions(outboxReader);
		verify(outboxEventPublishExecutor, never()).appendTasks(1L);
		verify(outboxEventPublishExecutor, never()).markPublished(1L);
		verify(outboxEventPublishExecutor, never()).markFailed(1L);
	}
}
