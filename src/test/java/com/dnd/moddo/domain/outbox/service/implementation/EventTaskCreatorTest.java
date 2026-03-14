package com.dnd.moddo.domain.outbox.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.impl.EventTaskCreator;
import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;

@ExtendWith(MockitoExtension.class)
class EventTaskCreatorTest {

	@Mock
	private EventTaskRepository eventTaskRepository;

	@InjectMocks
	private EventTaskCreator eventTaskCreator;

	@Test
	@DisplayName("이벤트 태스크를 생성해서 저장한다.")
	void create() {
		OutboxEvent outboxEvent = mock(OutboxEvent.class);
		EventTask savedTask = mock(EventTask.class);
		when(eventTaskRepository.saveAndFlush(any(EventTask.class))).thenReturn(savedTask);

		EventTask result = eventTaskCreator.create(outboxEvent, EventTaskType.REWARD_GRANT, 20L);

		ArgumentCaptor<EventTask> captor = ArgumentCaptor.forClass(EventTask.class);
		verify(eventTaskRepository).saveAndFlush(captor.capture());
		assertThat(captor.getValue().getOutboxEvent()).isEqualTo(outboxEvent);
		assertThat(captor.getValue().getTaskType()).isEqualTo(EventTaskType.REWARD_GRANT);
		assertThat(captor.getValue().getTargetUserId()).isEqualTo(20L);
		assertThat(result).isEqualTo(savedTask);
	}
}
