package com.dnd.moddo.domain.outbox.service.implementation;

import static org.assertj.core.api.Assertions.*;
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
import com.dnd.moddo.outbox.application.impl.OutboxEventPublishExecutor;
import com.dnd.moddo.outbox.application.impl.OutboxReader;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublishExecutorTest {

	@Mock
	private OutboxReader outboxReader;

	@Mock
	private EventTaskCreator eventTaskCreator;

	@Mock
	private MemberReader memberReader;

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@InjectMocks
	private OutboxEventPublishExecutor outboxEventPublishExecutor;

	@Test
	@DisplayName("claimProcessing은 선점 update 결과를 boolean으로 반환한다.")
	void claimProcessing() {
		when(outboxEventRepository.claimProcessing(1L, OutboxEventStatus.PROCESSING, OutboxEventStatus.PENDING))
			.thenReturn(1)
			.thenReturn(0);

		boolean claimed = outboxEventPublishExecutor.claimProcessing(1L);
		boolean notClaimed = outboxEventPublishExecutor.claimProcessing(1L);

		org.assertj.core.api.Assertions.assertThat(claimed).isTrue();
		org.assertj.core.api.Assertions.assertThat(notClaimed).isFalse();
	}

	@Test
	@DisplayName("SETTLEMENT_COMPLETED 이벤트는 구성원별 태스크를 생성한다.")
	void appendTasksForSettlementCompleted() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		Member first = mock(Member.class);
		Member second = mock(Member.class);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getEventType()).thenReturn(OutboxEventType.SETTLEMENT_COMPLETED);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(memberReader.findAssignedMembersBySettlementId(10L)).thenReturn(List.of(first, second));
		when(first.getUserId()).thenReturn(20L);
		when(second.getUserId()).thenReturn(30L);

		outboxEventPublishExecutor.appendTasks(1L);

		ArgumentCaptor<List<EventTask>> captor = ArgumentCaptor.forClass(List.class);
		verify(eventTaskCreator).createAll(captor.capture());
		assertThat(captor.getValue()).hasSize(4);
		assertThat(captor.getValue())
			.extracting(EventTask::getTaskType, EventTask::getTargetUserId)
			.containsExactly(
				tuple(EventTaskType.REWARD_GRANT, 20L),
				tuple(EventTaskType.NOTIFICATION_SEND, 20L),
				tuple(EventTaskType.REWARD_GRANT, 30L),
				tuple(EventTaskType.NOTIFICATION_SEND, 30L)
			);
	}

	@Test
	@DisplayName("할당된 구성원이 없으면 태스크를 추가하지 않는다.")
	void skipAppendTasksWhenNoAssignedMembers() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);
		when(outboxEvent.getEventType()).thenReturn(OutboxEventType.SETTLEMENT_COMPLETED);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(memberReader.findAssignedMembersBySettlementId(10L)).thenReturn(List.of());

		outboxEventPublishExecutor.appendTasks(1L);

		verify(eventTaskCreator, never()).createAll(anyList());
	}

	@Test
	@DisplayName("markPublished는 아웃박스 이벤트를 published 상태로 변경한다.")
	void markPublished() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);

		outboxEventPublishExecutor.markPublished(1L);

		verify(outboxEvent).markPublished();
	}

	@Test
	@DisplayName("markFailed는 아웃박스 이벤트를 failed 상태로 변경한다.")
	void markFailed() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxReader.findById(1L)).thenReturn(outboxEvent);

		outboxEventPublishExecutor.markFailed(1L);

		verify(outboxEvent).markFailed();
	}
}
