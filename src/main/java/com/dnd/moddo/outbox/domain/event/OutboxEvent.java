package com.dnd.moddo.outbox.domain.event;

import java.time.LocalDateTime;

import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "outbox")
@Entity
public class OutboxEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OutboxEventType eventType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AggregateType aggregateType;

	@Column(nullable = false)
	private Long aggregateId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OutboxEventStatus status;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime publishedAt;

	@Builder
	private OutboxEvent(OutboxEventType eventType, AggregateType aggregateType, Long aggregateId) {
		this.eventType = eventType;
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.status = OutboxEventStatus.PENDING;
		this.createdAt = LocalDateTime.now();
	}

	public static OutboxEvent pending(OutboxEventType eventType, AggregateType aggregateType, Long aggregateId) {
		return OutboxEvent.builder()
			.eventType(eventType)
			.aggregateType(aggregateType)
			.aggregateId(aggregateId)
			.build();
	}

	public void markPublished() {
		this.status = OutboxEventStatus.PUBLISHED;
		this.publishedAt = LocalDateTime.now();
	}

	public void markFailed() {
		this.status = OutboxEventStatus.FAILED;
	}
}
