package com.dnd.moddo.outbox.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {
	private final OutboxEventRepository outboxEventRepository;
	private final OutboxEventTaskAppender outboxEventTaskAppender;

	@Transactional
	public void publishPendingEvents() {
		List<OutboxEvent> pendingEvents = outboxEventRepository.findAllByStatus((OutboxEventStatus.PENDING));

		for (OutboxEvent outboxEvent : pendingEvents) {
			publish(outboxEvent.getId());
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void publish(Long outboxEventId) {
		OutboxEvent outboxEvent = outboxEventRepository.getById(outboxEventId);
		if (outboxEvent.getStatus() != OutboxEventStatus.PENDING) {
			return;
		}

		try {
			outboxEventTaskAppender.appendTasks(outboxEvent);
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
}
