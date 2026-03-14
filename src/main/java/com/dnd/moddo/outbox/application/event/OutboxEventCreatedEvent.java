package com.dnd.moddo.outbox.application.event;

public record OutboxEventCreatedEvent(Long outboxEventId) {
	public OutboxEventCreatedEvent {
		if (outboxEventId <= 0) {
			throw new IllegalArgumentException("outboxEventId가 0이상이어야 합니다.");
		}
	}
}
