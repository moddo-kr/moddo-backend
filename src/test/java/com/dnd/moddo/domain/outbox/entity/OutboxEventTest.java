package com.dnd.moddo.domain.outbox.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

class OutboxEventTest {

	@Test
	@DisplayName("pending outbox event를 생성하면 기본 상태가 설정된다.")
	void createPendingOutboxEvent() {
		OutboxEvent outboxEvent = OutboxEvent.pending(
			OutboxEventType.SETTLEMENT_COMPLETED,
			AggregateType.SETTLEMENT,
			1L
		);

		assertThat(outboxEvent.getEventType()).isEqualTo(OutboxEventType.SETTLEMENT_COMPLETED);
		assertThat(outboxEvent.getAggregateType()).isEqualTo(AggregateType.SETTLEMENT);
		assertThat(outboxEvent.getAggregateId()).isEqualTo(1L);
		assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
		assertThat(outboxEvent.getCreatedAt()).isNotNull();
		assertThat(outboxEvent.getPublishedAt()).isNull();
	}

	@Test
	@DisplayName("publish 처리되면 published 상태와 시각이 설정된다.")
	void markPublished() {
		OutboxEvent outboxEvent = OutboxEvent.pending(
			OutboxEventType.SETTLEMENT_COMPLETED,
			AggregateType.SETTLEMENT,
			1L
		);

		outboxEvent.markPublished();

		assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
		assertThat(outboxEvent.getPublishedAt()).isNotNull();
	}

	@Test
	@DisplayName("실패 처리되면 failed 상태가 된다.")
	void markFailed() {
		OutboxEvent outboxEvent = OutboxEvent.pending(
			OutboxEventType.SETTLEMENT_COMPLETED,
			AggregateType.SETTLEMENT,
			1L
		);

		outboxEvent.markFailed();

		assertThat(outboxEvent.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
	}
}
