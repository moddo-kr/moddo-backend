package com.dnd.moddo.outbox.application.impl;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dnd.moddo.outbox.application.command.CommandEventTaskService;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventTaskScheduler {
	private final EventTaskRepository eventTaskRepository;
	private final CommandEventTaskService commandEventTaskService;

	@Scheduled(fixedDelay = 5000)
	public void processPendingTasks() {
		for (EventTask eventTask : eventTaskRepository.findTop30ByStatusInAndAttemptCountLessThanOrderByCreatedAtAsc(
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			EventTaskRetryPolicy.MAX_RETRY_COUNT
		)) {
			commandEventTaskService.process(eventTask.getId());
		}
	}
}
