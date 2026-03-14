package com.dnd.moddo.domain.common.logging;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.logging.DiscordMessage;
import com.dnd.moddo.common.logging.ErrorNotifier;
import com.dnd.moddo.common.logging.EventTaskFailureNotifier;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;

@ExtendWith(MockitoExtension.class)
class EventTaskFailureNotifierTest {

	@Mock
	private ErrorNotifier errorNotifier;

	@InjectMocks
	private EventTaskFailureNotifier eventTaskFailureNotifier;

	@Test
	@DisplayName("재시도 초과한 이벤트 태스크 정보를 Discord 알림으로 전송한다.")
	void notifyRetryExhausted() {
		OutboxEvent outboxEvent = OutboxEvent.pending(
			OutboxEventType.SETTLEMENT_COMPLETED,
			AggregateType.SETTLEMENT,
			1L
		);
		setOutboxEventId(outboxEvent, 10L);

		EventTask eventTask = EventTask.pending(outboxEvent, EventTaskType.REWARD_GRANT, 20L);
		setEventTaskId(eventTask, 30L);
		eventTask.markFailed("failed-1");
		eventTask.markFailed("failed-2");

		eventTaskFailureNotifier.notifyRetryExhausted(eventTask);

		ArgumentCaptor<DiscordMessage> captor = ArgumentCaptor.forClass(DiscordMessage.class);
		verify(errorNotifier).notifyError(captor.capture());

		DiscordMessage message = captor.getValue();
		assertThat(message.content()).isEqualTo("# EventTask 재시도 초과");
		assertThat(message.embeds()).hasSize(1);
		assertThat(message.embeds().get(0).getDescription())
			.contains("taskId: 30")
			.contains("taskType: REWARD_GRANT")
			.contains("targetUserId: 20")
			.contains("aggregateId: 1")
			.contains("attemptCount: 2")
			.contains("lastErrorMessage: failed-2");
	}

	private void setOutboxEventId(OutboxEvent outboxEvent, Long id) {
		try {
			java.lang.reflect.Field idField = OutboxEvent.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(outboxEvent, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void setEventTaskId(EventTask eventTask, Long id) {
		try {
			java.lang.reflect.Field idField = EventTask.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(eventTask, id);
		} catch (ReflectiveOperationException exception) {
			throw new RuntimeException(exception);
		}
	}
}
