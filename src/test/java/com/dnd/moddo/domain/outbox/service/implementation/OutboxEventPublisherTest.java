package com.dnd.moddo.domain.outbox.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import com.dnd.moddo.outbox.application.impl.OutboxReader;
import com.dnd.moddo.outbox.application.impl.OutboxEventPublishExecutor;
import com.dnd.moddo.outbox.application.impl.OutboxEventPublisher;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;

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
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(true);
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
		Slice<OutboxEvent> pendingEvents = new PageImpl<>(List.of(first, second), PageRequest.of(0, 100), 2);
		when(first.getId()).thenReturn(1L);
		when(second.getId()).thenReturn(2L);
		when(outboxReader.findByStatus(OutboxEventStatus.PENDING, PageRequest.of(0, 100))).thenReturn(pendingEvents);
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(true);
		when(outboxEventPublishExecutor.claimProcessing(2L)).thenReturn(true);

		outboxEventPublisher.publishPendingEvents();

		verify(outboxEventPublishExecutor).appendTasks(1L);
		verify(outboxEventPublishExecutor).markPublished(1L);
		verify(outboxEventPublishExecutor).appendTasks(2L);
		verify(outboxEventPublishExecutor).markPublished(2L);
	}

	@Test
	@DisplayName("배치 발행 중 한 이벤트가 실패해도 다음 pending 이벤트 처리를 계속한다.")
	void continuePublishingPendingEventsWhenOneEventFails() {
		OutboxEvent first = mock(OutboxEvent.class);
		OutboxEvent second = mock(OutboxEvent.class);
		Slice<OutboxEvent> pendingEvents = new PageImpl<>(List.of(first, second), PageRequest.of(0, 100), 2);
		when(first.getId()).thenReturn(1L);
		when(second.getId()).thenReturn(2L);
		when(outboxReader.findByStatus(OutboxEventStatus.PENDING, PageRequest.of(0, 100))).thenReturn(pendingEvents);
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(true);
		when(outboxEventPublishExecutor.claimProcessing(2L)).thenReturn(true);
		doThrow(new RuntimeException("append failed")).when(outboxEventPublishExecutor).appendTasks(1L);
		doThrow(new RuntimeException("mark failed")).when(outboxEventPublishExecutor).markFailed(1L);

		outboxEventPublisher.publishPendingEvents();

		verify(outboxEventPublishExecutor).appendTasks(1L);
		verify(outboxEventPublishExecutor).markFailed(1L);
		verify(outboxEventPublishExecutor).appendTasks(2L);
		verify(outboxEventPublishExecutor).markPublished(2L);
	}

	@Test
	@DisplayName("이미 다른 트랜잭션이 선점했으면 publish를 건너뛴다.")
	void skipWhenOutboxEventAlreadyProcessed() {
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(false);

		outboxEventPublisher.publish(1L);

		verify(outboxEventPublishExecutor, never()).appendTasks(1L);
		verify(outboxEventPublishExecutor, never()).markPublished(1L);
		verify(outboxEventPublishExecutor, never()).markFailed(1L);
	}

	@Test
	@DisplayName("failed 상태 변경에도 실패하면 예외를 다시 던진다.")
	void throwWhenMarkFailedThrowsException() {
		when(outboxEventPublishExecutor.claimProcessing(1L)).thenReturn(true);
		doThrow(new RuntimeException("append failed")).when(outboxEventPublishExecutor).appendTasks(1L);
		doThrow(new RuntimeException("mark failed")).when(outboxEventPublishExecutor).markFailed(1L);

		assertThatThrownBy(() -> outboxEventPublisher.publish(1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("mark failed");

		verify(outboxEventPublishExecutor).markFailed(1L);
		verify(outboxEventPublishExecutor, never()).markPublished(1L);
	}
}
