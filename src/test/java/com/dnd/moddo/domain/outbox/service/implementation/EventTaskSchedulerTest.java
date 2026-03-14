package com.dnd.moddo.domain.outbox.service.implementation;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.CommandEventTaskService;
import com.dnd.moddo.outbox.application.impl.EventTaskRetryPolicy;
import com.dnd.moddo.outbox.application.impl.EventTaskScheduler;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;

@ExtendWith(MockitoExtension.class)
class EventTaskSchedulerTest {

	@Mock
	private EventTaskRepository eventTaskRepository;

	@Mock
	private CommandEventTaskService commandEventTaskService;

	@InjectMocks
	private EventTaskScheduler eventTaskScheduler;

	@Test
	@DisplayName("스케줄러는 처리 대상 태스크를 최대 30개 조회해 순서대로 처리한다.")
	void processPendingTasks() {
		EventTask first = mock(EventTask.class);
		EventTask second = mock(EventTask.class);
		when(first.getId()).thenReturn(1L);
		when(second.getId()).thenReturn(2L);
		when(eventTaskRepository.findTop30ByStatusInAndAttemptCountLessThanOrderByCreatedAtAsc(
			List.of(EventTaskStatus.PENDING, EventTaskStatus.FAILED),
			EventTaskRetryPolicy.MAX_RETRY_COUNT
		)).thenReturn(List.of(first, second));

		eventTaskScheduler.processPendingTasks();

		verify(commandEventTaskService).process(1L);
		verify(commandEventTaskService).process(2L);
	}
}
