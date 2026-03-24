package com.dnd.moddo.domain.outbox.service;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.logging.EventTaskFailureNotifier;
import com.dnd.moddo.outbox.application.command.CommandEventTaskService;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;
import com.dnd.moddo.reward.application.RewardService;

@ExtendWith(MockitoExtension.class)
class CommandEventTaskServiceProcessTest {

	@Mock
	private EventTaskRepository eventTaskRepository;

	@Mock
	private RewardService rewardService;

	@Mock
	private EventTaskFailureNotifier eventTaskFailureNotifier;

	@InjectMocks
	private CommandEventTaskService commandEventTaskService;

	@Test
	@DisplayName("선점에 실패한 태스크는 처리하지 않는다.")
	void skipWhenClaimProcessingFails() {
		when(eventTaskRepository.claimProcessing(
			1L,
			EventTaskStatus.PROCESSING,
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			5
		)).thenReturn(0);

		commandEventTaskService.process(1L);

		verify(eventTaskRepository, never()).getById(anyLong());
		verifyNoInteractions(rewardService);
	}

	@Test
	@DisplayName("REWARD_GRANT 태스크를 성공적으로 처리하면 완료 처리한다.")
	void processRewardGrantTask() {
		EventTask eventTask = mock(EventTask.class);
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(eventTaskRepository.claimProcessing(
			1L,
			EventTaskStatus.PROCESSING,
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			5
		)).thenReturn(1);
		when(eventTaskRepository.getById(1L)).thenReturn(eventTask);
		when(eventTask.getTaskType()).thenReturn(EventTaskType.REWARD_GRANT);
		when(eventTask.getOutboxEvent()).thenReturn(outboxEvent);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(eventTask.getTargetUserId()).thenReturn(20L);

		commandEventTaskService.process(1L);

		verify(eventTaskRepository).claimProcessing(
			1L,
			EventTaskStatus.PROCESSING,
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			5
		);
		verify(rewardService).grant(10L, 20L);
		verify(eventTask).markCompleted();
		verify(eventTask, never()).markFailed(anyString());
	}

	@Test
	@DisplayName("실패했지만 최대 재시도 전이면 운영 알림은 보내지 않는다.")
	void doesNotNotifyWhenRetryNotExhausted() {
		EventTask eventTask = mock(EventTask.class);
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(eventTaskRepository.claimProcessing(
			1L,
			EventTaskStatus.PROCESSING,
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			5
		)).thenReturn(1);
		when(eventTaskRepository.getById(1L)).thenReturn(eventTask);
		when(eventTask.getTaskType()).thenReturn(EventTaskType.REWARD_GRANT);
		when(eventTask.getOutboxEvent()).thenReturn(outboxEvent);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(eventTask.getTargetUserId()).thenReturn(20L);
		doThrow(new RuntimeException("grant failed")).when(rewardService).grant(10L, 20L);
		when(eventTask.getAttemptCount()).thenReturn(3);

		commandEventTaskService.process(1L);

		verify(eventTask).markFailed("grant failed");
		verify(eventTaskFailureNotifier, never()).notifyRetryExhausted(any());
	}

	@Test
	@DisplayName("최대 재시도에 도달한 실패 태스크는 운영 알림을 보낸다.")
	void notifyWhenRetryExhausted() {
		EventTask eventTask = mock(EventTask.class);
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		when(eventTaskRepository.claimProcessing(
			1L,
			EventTaskStatus.PROCESSING,
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			5
		)).thenReturn(1);
		when(eventTaskRepository.getById(1L)).thenReturn(eventTask);
		when(eventTask.getTaskType()).thenReturn(EventTaskType.REWARD_GRANT);
		when(eventTask.getOutboxEvent()).thenReturn(outboxEvent);
		when(outboxEvent.getAggregateId()).thenReturn(10L);
		when(eventTask.getTargetUserId()).thenReturn(20L);
		doThrow(new RuntimeException("grant failed")).when(rewardService).grant(10L, 20L);
		when(eventTask.getAttemptCount()).thenReturn(5);

		commandEventTaskService.process(1L);

		verify(eventTask).markFailed("grant failed");
		verify(eventTask).markDead("grant failed");
		verify(eventTaskFailureNotifier).notifyRetryExhausted(eventTask);
	}

	@Test
	@DisplayName("지원하지 않는 태스크 타입은 실패 처리한다.")
	void failWhenTaskTypeUnsupported() {
		EventTask eventTask = mock(EventTask.class);
		when(eventTaskRepository.claimProcessing(
			1L,
			EventTaskStatus.PROCESSING,
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			5
		)).thenReturn(1);
		when(eventTaskRepository.getById(1L)).thenReturn(eventTask);
		when(eventTask.getTaskType()).thenReturn(EventTaskType.NOTIFICATION_SEND);
		when(eventTask.getAttemptCount()).thenReturn(1);

		commandEventTaskService.process(1L);

		verifyNoInteractions(rewardService);
		verify(eventTask).markFailed("Unsupported task type: NOTIFICATION_SEND");
		verify(eventTask, never()).markCompleted();
		verify(eventTask, never()).markDead(anyString());
		verify(eventTaskFailureNotifier, never()).notifyRetryExhausted(any());
	}
}
