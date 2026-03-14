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

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.outbox.application.impl.EventTaskCreator;
import com.dnd.moddo.outbox.application.impl.OutboxReader;
import com.dnd.moddo.outbox.application.impl.OutboxEventPublisher;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

	@Mock
	private OutboxReader outboxReader;

	@Mock
	private EventTaskCreator eventTaskCreator;

	@Mock
	private MemberReader memberReader;

	@InjectMocks
	private OutboxEventPublisher outboxEventPublisher;

	@Test
	@DisplayName("PENDING 아웃박스 이벤트를 publish하면 태스크를 추가하고 published 상태로 변경한다.")
	void publishPendingOutboxEvent() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		Member member = mock(Member.class);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getStatus()).thenReturn(OutboxEventStatus.PENDING);
		when(outboxEvent.getEventType()).thenReturn(OutboxEventType.SETTLEMENT_COMPLETED);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(memberReader.findAssignedMembersBySettlementId(10L)).thenReturn(List.of(member));
		when(member.getUserId()).thenReturn(20L);

		outboxEventPublisher.publish(1L);

		verify(eventTaskCreator).create(outboxEvent, EventTaskType.REWARD_GRANT, 20L);
		verify(eventTaskCreator).create(outboxEvent, EventTaskType.NOTIFICATION_SEND, 20L);
		verify(outboxEvent).markPublished();
		verify(outboxEvent, never()).markFailed();
	}

	@Test
	@DisplayName("태스크 추가 중 예외가 발생하면 failed 상태로 변경한다.")
	void markFailedWhenAppendTaskThrowsException() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getStatus()).thenReturn(OutboxEventStatus.PENDING);
		when(outboxEvent.getEventType()).thenReturn(OutboxEventType.SETTLEMENT_COMPLETED);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		doThrow(new RuntimeException("append failed")).when(memberReader).findAssignedMembersBySettlementId(10L);

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
		when(outboxReader.findAllByStatus(OutboxEventStatus.PENDING)).thenReturn(List.of(first, second));
		when(outboxReader.findById(1L)).thenReturn(first);
		when(outboxReader.findById(2L)).thenReturn(second);
		when(first.getStatus()).thenReturn(OutboxEventStatus.PENDING);
		when(second.getStatus()).thenReturn(OutboxEventStatus.PENDING);
		when(first.getEventType()).thenReturn(OutboxEventType.SETTLEMENT_COMPLETED);
		when(second.getEventType()).thenReturn(OutboxEventType.SETTLEMENT_COMPLETED);
		when(first.getAggregateId()).thenReturn(10L);
		when(second.getAggregateId()).thenReturn(20L);
		when(memberReader.findAssignedMembersBySettlementId(10L)).thenReturn(List.of());
		when(memberReader.findAssignedMembersBySettlementId(20L)).thenReturn(List.of());

		outboxEventPublisher.publishPendingEvents();

		verify(first).markPublished();
		verify(second).markPublished();
	}

	@Test
	@DisplayName("pending 상태가 아니면 publish를 건너뛴다.")
	void skipWhenOutboxEventAlreadyProcessed() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getStatus()).thenReturn(OutboxEventStatus.PUBLISHED);

		outboxEventPublisher.publish(1L);

		verifyNoInteractions(eventTaskCreator, memberReader);
		verify(outboxEvent, never()).markPublished();
		verify(outboxEvent, never()).markFailed();
	}
}
