package com.dnd.moddo.outbox.application.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dnd.moddo.common.logging.DiscordMessage;
import com.dnd.moddo.common.logging.ErrorNotifier;
import com.dnd.moddo.outbox.domain.task.EventTask;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventTaskFailureNotifier {
	private final ErrorNotifier errorNotifier;

	public void notifyRetryExhausted(EventTask eventTask) {
		errorNotifier.notifyError(
			DiscordMessage.createDiscordMessage(
				"# EventTask 재시도 초과",
				List.of(
					DiscordMessage.Embed.builder()
						.title("EventTask 최종 실패")
						.description(
							"taskId: " + eventTask.getId() + "\n" +
								"taskType: " + eventTask.getTaskType() + "\n" +
								"targetUserId: " + eventTask.getTargetUserId() + "\n" +
								"aggregateId: " + eventTask.getOutboxEvent().getAggregateId() + "\n" +
								"attemptCount: " + eventTask.getAttemptCount() + "\n" +
								"lastErrorMessage: " + eventTask.getLastErrorMessage()
						)
						.build()
				)
			)
		);
	}
}
