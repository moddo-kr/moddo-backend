package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.impl.EventTaskFailureNotifier;
import com.dnd.moddo.outbox.application.impl.EventTaskProcessor;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;
import com.dnd.moddo.reward.application.impl.RewardGrantHandler;

@ExtendWith(MockitoExtension.class)
class EventTaskProcessorTest {

	@Mock
	private EventTaskRepository eventTaskRepository;

	@Mock
	private RewardGrantHandler rewardGrantHandler;

	@Mock
	private EventTaskFailureNotifier eventTaskFailureNotifier;

	@InjectMocks
	private EventTaskProcessor eventTaskProcessor;

	@Test
	@DisplayName("완료된 태스크는 다시 처리하지 않는다.")
	void skipCompletedTask() {
		EventTask eventTask = mock(EventTask.class);
		when(eventTaskRepository.getById(1L)).thenReturn(eventTask);
		when(eventTask.getStatus()).thenReturn(EventTaskStatus.COMPLETED);

		eventTaskProcessor.process(1L);

		verify(eventTask, never()).markProcessing();
		verifyNoInteractions(rewardGrantHandler);
	}

	@Test
	@DisplayName("REWARD_GRANT 태스크를 성공적으로 처리하면 완료 처리한다.")
	void processRewardGrantTask() {
		EventTask eventTask = mock(EventTask.class);
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(eventTaskRepository.getById(1L)).thenReturn(eventTask);
		when(eventTask.getStatus()).thenReturn(EventTaskStatus.PENDING);
		when(eventTask.getTaskType()).thenReturn(EventTaskType.REWARD_GRANT);
		when(eventTask.getOutboxEvent()).thenReturn(outboxEvent);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(eventTask.getTargetUserId()).thenReturn(20L);

		eventTaskProcessor.process(1L);

		verify(eventTask).markProcessing();
		verify(rewardGrantHandler).handle(10L, 20L);
		verify(eventTask).markCompleted();
		verify(eventTask, never()).markFailed(anyString());
	}

	@Test
	@DisplayName("최대 재시도에 도달한 실패 태스크는 운영 알림을 보낸다.")
	void notifyWhenRetryExhausted() {
		EventTask eventTask = mock(EventTask.class);
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(eventTaskRepository.getById(1L)).thenReturn(eventTask);
		when(eventTask.getStatus()).thenReturn(EventTaskStatus.PENDING);
		when(eventTask.getTaskType()).thenReturn(EventTaskType.REWARD_GRANT);
		when(eventTask.getOutboxEvent()).thenReturn(outboxEvent);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(eventTask.getTargetUserId()).thenReturn(20L);
		doThrow(new RuntimeException("grant failed")).when(rewardGrantHandler).handle(10L, 20L);
		when(eventTask.getAttemptCount()).thenReturn(5);

		eventTaskProcessor.process(1L);

		verify(eventTask).markFailed("grant failed");
		verify(eventTaskFailureNotifier).notifyRetryExhausted(eventTask);
	}
}
