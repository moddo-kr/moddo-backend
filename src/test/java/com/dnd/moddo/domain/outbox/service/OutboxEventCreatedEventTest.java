package com.dnd.moddo.domain.outbox.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.moddo.outbox.application.event.OutboxEventCreatedEvent;

class OutboxEventCreatedEventTest {

	@Test
	@DisplayName("양수 outboxEventId로 이벤트를 생성할 수 있다.")
	void createEvent() {
		OutboxEventCreatedEvent event = new OutboxEventCreatedEvent(1L);

		assertThat(event.outboxEventId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("0 이하 outboxEventId로 이벤트를 생성하면 예외가 발생한다.")
	void throwExceptionWhenOutboxEventIdIsNotPositive() {
		assertThatThrownBy(() -> new OutboxEventCreatedEvent(0L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("outboxEventId가 0이상이어야 합니다.");

		assertThatThrownBy(() -> new OutboxEventCreatedEvent(-1L))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("outboxEventId가 0이상이어야 합니다.");
	}
}
