package com.dnd.moddo.outbox.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;
import com.dnd.moddo.reward.application.impl.RewardGrantHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventTaskProcessor {
	private final EventTaskRepository eventTaskRepository;
	private final RewardGrantHandler rewardGrantHandler;
	private final EventTaskFailureNotifier eventTaskFailureNotifier;

	@Transactional
	public void process(Long eventTaskId) {
		EventTask eventTask = eventTaskRepository.getById(eventTaskId);
		if (eventTask.getStatus() == EventTaskStatus.COMPLETED) {
			return;
		}

		eventTask.markProcessing();

		try {
			if (eventTask.getTaskType() == EventTaskType.REWARD_GRANT) {
				rewardGrantHandler.handle(eventTask.getOutboxEvent().getAggregateId(), eventTask.getTargetUserId());
			}

			eventTask.markCompleted();
		} catch (Exception exception) {
			eventTask.markFailed(exception.getMessage());
			if (eventTask.getAttemptCount() >= EventTaskRetryPolicy.MAX_RETRY_COUNT) {
				eventTaskFailureNotifier.notifyRetryExhausted(eventTask);
			}
		}
	}
}
