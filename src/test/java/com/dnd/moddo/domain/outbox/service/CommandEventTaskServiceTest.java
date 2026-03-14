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
class CommandEventTaskServiceTest {

	@Mock
	private EventTaskRepository eventTaskRepository;

	@Mock
	private RewardService rewardService;

	@Mock
	private EventTaskFailureNotifier eventTaskFailureNotifier;

	@InjectMocks
	private CommandEventTaskService commandEventTaskService;

	@Test
	@DisplayName("이벤트 태스크 재처리를 요청할 수 있다.")
	void retry() {
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

		commandEventTaskService.retry(1L);

		verify(rewardService).grant(10L, 20L);
	}
}
