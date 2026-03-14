package com.dnd.moddo.domain.outbox.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;

class EventTaskTest {

	@Test
	@DisplayName("pending event task를 생성하면 기본 상태가 설정된다.")
	void createPendingEventTask() {
		OutboxEvent outboxEvent = OutboxEvent.pending(
			OutboxEventType.SETTLEMENT_COMPLETED,
			AggregateType.SETTLEMENT,
			1L
		);

		EventTask eventTask = EventTask.pending(outboxEvent, EventTaskType.REWARD_GRANT, 10L);

		assertThat(eventTask.getOutboxEvent()).isEqualTo(outboxEvent);
		assertThat(eventTask.getTaskType()).isEqualTo(EventTaskType.REWARD_GRANT);
		assertThat(eventTask.getTargetUserId()).isEqualTo(10L);
		assertThat(eventTask.getStatus()).isEqualTo(EventTaskStatus.PENDING);
		assertThat(eventTask.getAttemptCount()).isZero();
		assertThat(eventTask.getCreatedAt()).isNotNull();
	}

	@Test
	@DisplayName("처리 중 상태로 변경할 수 있다.")
	void markProcessing() {
		EventTask eventTask = EventTask.pending(
			OutboxEvent.pending(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L),
			EventTaskType.REWARD_GRANT,
			10L
		);

		eventTask.markProcessing();

		assertThat(eventTask.getStatus()).isEqualTo(EventTaskStatus.PROCESSING);
	}

	@Test
	@DisplayName("완료 처리되면 completed 상태와 처리 시각이 설정된다.")
	void markCompleted() {
		EventTask eventTask = EventTask.pending(
			OutboxEvent.pending(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L),
			EventTaskType.REWARD_GRANT,
			10L
		);

		eventTask.markCompleted();

		assertThat(eventTask.getStatus()).isEqualTo(EventTaskStatus.COMPLETED);
		assertThat(eventTask.getProcessedAt()).isNotNull();
		assertThat(eventTask.getLastErrorMessage()).isNull();
	}

	@Test
	@DisplayName("실패 처리되면 failed 상태와 시도 횟수, 오류 메시지가 기록된다.")
	void markFailed() {
		EventTask eventTask = EventTask.pending(
			OutboxEvent.pending(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L),
			EventTaskType.REWARD_GRANT,
			10L
		);

		eventTask.markFailed("failed");

		assertThat(eventTask.getStatus()).isEqualTo(EventTaskStatus.FAILED);
		assertThat(eventTask.getAttemptCount()).isEqualTo(1);
		assertThat(eventTask.getLastErrorMessage()).isEqualTo("failed");
	}
}
