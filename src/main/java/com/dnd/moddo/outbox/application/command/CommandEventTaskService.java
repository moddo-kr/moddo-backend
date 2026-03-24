package com.dnd.moddo.outbox.application.command;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.common.logging.EventTaskFailureNotifier;
import com.dnd.moddo.outbox.application.impl.EventTaskRetryPolicy;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;
import com.dnd.moddo.reward.application.RewardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandEventTaskService {
	private final EventTaskRepository eventTaskRepository;
	private final RewardService rewardService;
	private final EventTaskFailureNotifier eventTaskFailureNotifier;

	@Transactional
	public void process(Long eventTaskId) {
		int updatedCount = eventTaskRepository.claimProcessing(
			eventTaskId,
			EventTaskStatus.PROCESSING,
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			EventTaskRetryPolicy.MAX_RETRY_COUNT
		);
		if (updatedCount == 0) {
			return;
		}

		EventTask eventTask = eventTaskRepository.getById(eventTaskId);

		try {
			if (eventTask.getTaskType() == EventTaskType.REWARD_GRANT) {
				rewardService.grant(eventTask.getOutboxEvent().getAggregateId(), eventTask.getTargetUserId());
			} else {
				throw new IllegalStateException("Unsupported task type: " + eventTask.getTaskType());
			}

			eventTask.markCompleted();
		} catch (Exception exception) {
			eventTask.markFailed(exception.getMessage());
			if (eventTask.getAttemptCount() >= EventTaskRetryPolicy.MAX_RETRY_COUNT) {
				eventTask.markDead(exception.getMessage());
				eventTaskFailureNotifier.notifyRetryExhausted(eventTask);
			}
		}
	}

	@Transactional
	public void retry(Long eventTaskId) {
		process(eventTaskId);
	}
}
