package com.dnd.moddo.outbox.application.impl;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {
	private static final int PENDING_OUTBOX_BATCH_SIZE = 100;

	private final OutboxReader outboxReader;
	private final OutboxEventPublishExecutor outboxEventPublishExecutor;

	public void publishPendingEvents() {
		Slice<OutboxEvent> pendingEvents = outboxReader.findByStatus(
			OutboxEventStatus.PENDING,
			PageRequest.of(0, PENDING_OUTBOX_BATCH_SIZE)
		);

		for (OutboxEvent outboxEvent : pendingEvents.getContent()) {
			try {
				publish(outboxEvent.getId());
			} catch (Exception e) {
				log.error("Error publishing event to outbox publisher. outboxId={}", outboxEvent.getId(), e);
			}
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
			log.error("Failed to publish outbox event. outboxEventId={}", outboxEventId, exception);
			try {
				outboxEventPublishExecutor.markFailed(outboxEventId);
			} catch (Exception markFailedException) {
				log.error("Failed to mark outbox event as FAILED. outboxEventId={}", outboxEventId,
					markFailedException);
				throw markFailedException;
			}
		}
	}
}
