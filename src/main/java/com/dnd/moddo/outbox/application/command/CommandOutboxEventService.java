package com.dnd.moddo.outbox.application.command;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.application.event.OutboxEventCreatedEvent;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandOutboxEventService {
	private final OutboxEventRepository outboxEventRepository;
	private final ApplicationEventPublisher eventPublisher;

	public OutboxEvent create(OutboxEventType type, AggregateType aggregateType, Long aggregateId) {
		OutboxEvent outboxEvent = OutboxEvent.pending(type, aggregateType, aggregateId);
		OutboxEvent savedOutboxEvent = outboxEventRepository.save(outboxEvent);
		eventPublisher.publishEvent(new OutboxEventCreatedEvent(savedOutboxEvent.getId()));
		return savedOutboxEvent;
	}
}
