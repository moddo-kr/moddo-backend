package com.dnd.moddo.outbox.domain.task.type;

public enum EventTaskStatus {
	PENDING,
	PROCESSING,
	COMPLETED,
	FAILED,
	DEAD
}
