package com.dnd.moddo.outbox.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {
	private final OutboxReader outboxReader;
	private final EventTaskCreator eventTaskCreator;
	private final MemberReader memberReader;

	@Transactional
	public void publishPendingEvents() {
		List<OutboxEvent> pendingEvents = outboxReader.findAllByStatus(OutboxEventStatus.PENDING);

		for (OutboxEvent outboxEvent : pendingEvents) {
			publish(outboxEvent.getId());
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void publish(Long outboxEventId) {
		OutboxEvent outboxEvent = outboxReader.findById(outboxEventId);
		if (outboxEvent.getStatus() != OutboxEventStatus.PENDING) {
			return;
		}

		try {
			appendTasks(outboxEvent);
			outboxEvent.markPublished();
		} catch (Exception exception) {
			log.error("Failed to publish outbox event. outboxEventId={}, eventType={}, aggregateId={}",
				outboxEvent.getId(),
				outboxEvent.getEventType(),
				outboxEvent.getAggregateId(),
				exception);
			outboxEvent.markFailed();
		}
	}

	private void appendTasks(OutboxEvent outboxEvent) {
		if (outboxEvent.getEventType() == OutboxEventType.SETTLEMENT_COMPLETED) {
			appendSettlementCompletedTasks(outboxEvent);
		}
	}

	private void appendSettlementCompletedTasks(OutboxEvent outboxEvent) {
		for (Member member : memberReader.findAssignedMembersBySettlementId(outboxEvent.getAggregateId())) {
			Long targetUserId = member.getUserId();
			eventTaskCreator.create(outboxEvent, EventTaskType.REWARD_GRANT, targetUserId);
			eventTaskCreator.create(outboxEvent, EventTaskType.NOTIFICATION_SEND, targetUserId);
		}
	}
}
