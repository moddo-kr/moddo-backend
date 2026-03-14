package com.dnd.moddo.domain.outbox.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.dnd.moddo.outbox.application.event.OutboxEventCreatedEvent;
import com.dnd.moddo.outbox.application.impl.OutboxEventCreator;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

@ExtendWith(MockitoExtension.class)
class OutboxEventCreatorTest {

	@Mock
	private OutboxEventRepository outboxEventRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private OutboxEventCreator outboxEventCreator;

	@Test
	@DisplayName("아웃박스 이벤트를 저장하고 생성 이벤트를 발행한다.")
	void create() {
		OutboxEvent savedEvent = OutboxEvent.pending(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L);
		setOutboxEventId(savedEvent, 10L);
		when(outboxEventRepository.save(any(OutboxEvent.class))).thenReturn(savedEvent);

		OutboxEvent result = outboxEventCreator.create(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT, 1L);

		assertThat(result).isEqualTo(savedEvent);

		ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
		verify(outboxEventRepository).save(captor.capture());
		assertThat(captor.getValue().getEventType()).isEqualTo(OutboxEventType.SETTLEMENT_COMPLETED);
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
