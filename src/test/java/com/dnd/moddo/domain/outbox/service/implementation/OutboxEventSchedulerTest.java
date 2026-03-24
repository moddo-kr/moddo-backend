package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.impl.OutboxEventPublisher;
import com.dnd.moddo.outbox.application.impl.OutboxEventScheduler;

@ExtendWith(MockitoExtension.class)
class OutboxEventSchedulerTest {

	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	@InjectMocks
	private OutboxEventScheduler outboxEventScheduler;

	@Test
	@DisplayName("스케줄러는 pending 아웃박스 이벤트 발행을 주기적으로 위임한다.")
	void publishPendingEvents() {
		outboxEventScheduler.publishPendingEvents();

		verify(outboxEventPublisher).publishPendingEvents();
	}
}
