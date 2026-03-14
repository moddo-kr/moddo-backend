package com.dnd.moddo.outbox.application.impl;

import org.springframework.stereotype.Component;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventTaskAppender {
	private final EventTaskRepository eventTaskRepository;
	private final MemberReader memberReader;

	public void appendTasks(OutboxEvent outboxEvent) {
		if (outboxEvent.getEventType() == OutboxEventType.SETTLEMENT_COMPLETED) {
			appendSettlementCompletedTasks(outboxEvent);
		}
	}

	private void appendSettlementCompletedTasks(OutboxEvent outboxEvent) {
		for (Member member : memberReader.findAssignedMembersBySettlementId(outboxEvent.getAggregateId())) {
			Long targetUserId = member.getUserId();
			eventTaskRepository.saveAndFlush(EventTask.pending(outboxEvent, EventTaskType.REWARD_GRANT, targetUserId));
			eventTaskRepository.saveAndFlush(
				EventTask.pending(outboxEvent, EventTaskType.NOTIFICATION_SEND, targetUserId)
			);
		}
	}
}
