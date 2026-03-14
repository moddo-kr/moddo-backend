package com.dnd.moddo.outbox.application.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.application.impl.OutboxEventCreator;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandOutboxEventService {
	private final OutboxEventCreator outboxEventCreator;

	public OutboxEvent create(OutboxEventType type, AggregateType aggregateType, Long aggregateId) {
		return outboxEventCreator.create(type, aggregateType, aggregateId);
	}
}
