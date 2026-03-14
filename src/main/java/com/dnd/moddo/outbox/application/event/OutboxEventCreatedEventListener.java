package com.dnd.moddo.outbox.application.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.dnd.moddo.outbox.application.impl.OutboxEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxEventCreatedEventListener {
	private final OutboxEventPublisher outboxEventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(OutboxEventCreatedEvent event) {
		outboxEventPublisher.publish(event.outboxEventId());
	}
}
