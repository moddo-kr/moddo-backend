package com.dnd.moddo.outbox.domain.task;

import java.time.LocalDateTime;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_task")
@Entity
public class EventTask {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "outbox_id", nullable = false)
	private OutboxEvent outboxEvent;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventTaskType taskType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EventTaskStatus status;

	@Column(name = "target_user_id")
	private Long targetUserId;

	@Column(nullable = false)
	private int attemptCount;

	private String lastErrorMessage;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime processedAt;

	@Builder
	private EventTask(OutboxEvent outboxEvent, EventTaskType taskType, Long targetUserId) {
		this.outboxEvent = outboxEvent;
		this.taskType = taskType;
		this.targetUserId = targetUserId;
		this.status = EventTaskStatus.PENDING;
		this.attemptCount = 0;
		this.createdAt = LocalDateTime.now();
	}

	public static EventTask pending(OutboxEvent outboxEvent, EventTaskType taskType, Long targetUserId) {
		return EventTask.builder()
			.outboxEvent(outboxEvent)
			.taskType(taskType)
			.targetUserId(targetUserId)
			.build();
	}

	public void markProcessing() {
		this.status = EventTaskStatus.PROCESSING;
	}

	public void markCompleted() {
		this.status = EventTaskStatus.COMPLETED;
		this.processedAt = LocalDateTime.now();
		this.lastErrorMessage = null;
	}

	public void markFailed(String errorMessage) {
		this.status = EventTaskStatus.FAILED;
		this.attemptCount++;
		this.lastErrorMessage = errorMessage;
	}

	public void markDead(String errorMessage) {
		this.status = EventTaskStatus.DEAD;
		this.processedAt = LocalDateTime.now();
		this.lastErrorMessage = errorMessage;
	}
}
