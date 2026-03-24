package com.dnd.moddo.outbox.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {
	private final OutboxReader outboxReader;
	private final OutboxEventPublishExecutor outboxEventPublishExecutor;

	@Transactional
	public void publishPendingEvents() {
		List<OutboxEvent> pendingEvents = outboxReader.findAllByStatus(OutboxEventStatus.PENDING);

		for (OutboxEvent outboxEvent : pendingEvents) {
			publish(outboxEvent.getId());
		}
	}

	public void publish(Long outboxEventId) {
		if (!outboxEventPublishExecutor.claimProcessing(outboxEventId)) {
			return;
		}

		try {
			outboxEventPublishExecutor.appendTasks(outboxEventId);
			outboxEventPublishExecutor.markPublished(outboxEventId);
		} catch (Exception exception) {
			OutboxEvent outboxEvent = outboxReader.findById(outboxEventId);
			log.error("Failed to publish outbox event. outboxEventId={}, eventType={}, aggregateId={}",
				outboxEvent.getId(),
				outboxEvent.getEventType(),
				outboxEvent.getAggregateId(),
				exception);
			outboxEventPublishExecutor.markFailed(outboxEventId);
		}
	}
}
