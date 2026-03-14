package com.dnd.moddo.domain.outbox.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.command.CommandOutboxEventService;
import com.dnd.moddo.outbox.application.impl.OutboxEventCreator;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

@ExtendWith(MockitoExtension.class)
class CommandOutboxEventServiceTest {

	@Mock
	private OutboxEventCreator outboxEventCreator;

	@InjectMocks
	private CommandOutboxEventService commandOutboxEventService;

	@Test
	@DisplayName("아웃박스 이벤트 생성을 위임한다.")
	void create() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(outboxEventCreator.create(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L))
			.thenReturn(outboxEvent);

		OutboxEvent result = commandOutboxEventService.create(
			OutboxEventType.SETTLEMENT_COMPLETED,
			AggregateType.SETTLEMENT,
			1L
		);

		assertThat(result).isEqualTo(outboxEvent);
		verify(outboxEventCreator).create(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L);
	}
}
