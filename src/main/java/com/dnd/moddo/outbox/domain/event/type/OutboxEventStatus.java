package com.dnd.moddo.outbox.domain.event.type;

public enum OutboxEventStatus {
	PENDING,
	PROCESSING,
	PUBLISHED,
	FAILED,
}
