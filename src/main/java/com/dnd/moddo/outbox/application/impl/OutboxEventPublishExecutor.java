package com.dnd.moddo.outbox.application.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OutboxEventPublishExecutor {
	private final OutboxReader outboxReader;
	private final EventTaskCreator eventTaskCreator;
	private final MemberReader memberReader;
	private final OutboxEventRepository outboxEventRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean claimProcessing(Long outboxEventId) {
		int updatedCount = outboxEventRepository.claimProcessing(
			outboxEventId,
			OutboxEventStatus.PROCESSING,
			OutboxEventStatus.PENDING
		);
		return updatedCount > 0;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void appendTasks(Long outboxEventId) {
		OutboxEvent outboxEvent = outboxReader.findById(outboxEventId);
		if (outboxEvent.getEventType() == OutboxEventType.SETTLEMENT_COMPLETED) {
			appendSettlementCompletedTasks(outboxEvent);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markPublished(Long outboxEventId) {
		OutboxEvent outboxEvent = outboxReader.findById(outboxEventId);
		outboxEvent.markPublished();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markFailed(Long outboxEventId) {
		OutboxEvent outboxEvent = outboxReader.findById(outboxEventId);
		outboxEvent.markFailed();
	}

	private void appendSettlementCompletedTasks(OutboxEvent outboxEvent) {
		List<EventTask> eventTasks = new ArrayList<>();

		for (Member member : memberReader.findAssignedMembersBySettlementId(outboxEvent.getAggregateId())) {
			Long targetUserId = member.getUserId();
			eventTasks.add(EventTask.pending(outboxEvent, EventTaskType.REWARD_GRANT, targetUserId));
			eventTasks.add(EventTask.pending(outboxEvent, EventTaskType.NOTIFICATION_SEND, targetUserId));
		}

		if (!eventTasks.isEmpty()) {
			eventTaskCreator.createAll(eventTasks);
		}
	}
}
