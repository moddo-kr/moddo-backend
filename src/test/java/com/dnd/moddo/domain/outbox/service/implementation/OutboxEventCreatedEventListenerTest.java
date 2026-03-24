package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.event.OutboxEventCreatedEvent;
import com.dnd.moddo.outbox.application.event.OutboxEventCreatedEventListener;
import com.dnd.moddo.outbox.application.impl.OutboxEventPublisher;

@ExtendWith(MockitoExtension.class)
class OutboxEventCreatedEventListenerTest {

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@InjectMocks
	private OutboxEventCreatedEventListener listener;

	@Test
	@DisplayName("아웃박스 생성 이벤트를 받으면 publish를 호출한다.")
	void handle() {
		listener.handle(new OutboxEventCreatedEvent(1L));

		verify(outboxEventPublisher).publish(1L);
	}
}
