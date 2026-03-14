package com.dnd.moddo.domain.outbox.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.dnd.moddo.outbox.application.command.CommandOutboxEventService;
import com.dnd.moddo.outbox.application.event.OutboxEventCreatedEvent;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

@ExtendWith(MockitoExtension.class)
class CommandOutboxEventServiceTest {

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private CommandOutboxEventService commandOutboxEventService;

	@Test
	@DisplayName("아웃박스 이벤트를 저장하고 생성 이벤트를 발행한다.")
	void create() {
		OutboxEvent outboxEvent = OutboxEvent.pending(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT,
			1L);
		setOutboxEventId(outboxEvent, 10L);
		when(outboxEventRepository.save(any(OutboxEvent.class))).thenReturn(outboxEvent);

		OutboxEvent result = commandOutboxEventService.create(
			OutboxEventType.SETTLEMENT_COMPLETED,
			AggregateType.SETTLEMENT,
			1L
		);

		assertThat(result).isEqualTo(outboxEvent);
		verify(outboxEventRepository).save(any(OutboxEvent.class));
		verify(eventPublisher).publishEvent(new OutboxEventCreatedEvent(10L));
	}

	private void setOutboxEventId(OutboxEvent outboxEvent, Long id) {
		try {
			java.lang.reflect.Field idField = OutboxEvent.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(outboxEvent, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}
}
