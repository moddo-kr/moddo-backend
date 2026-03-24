package com.dnd.moddo.common.logging;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dnd.moddo.outbox.domain.task.EventTask;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventTaskFailureNotifier {
	private final ErrorNotifier errorNotifier;

	public void notifyRetryExhausted(EventTask eventTask) {
		try {
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
		} catch (Exception e) {
			log.warn("Failed to notify exhausted EventTask. taskId={}", eventTask.getId(), e);
		}
	}
}
