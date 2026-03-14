package com.dnd.moddo.domain.outbox.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.outbox.application.command.CommandEventTaskService;
import com.dnd.moddo.outbox.application.impl.EventTaskProcessor;

@ExtendWith(MockitoExtension.class)
class CommandEventTaskServiceTest {

	@Mock
	private EventTaskProcessor eventTaskProcessor;

	@InjectMocks
	private CommandEventTaskService commandEventTaskService;

	@Test
	@DisplayName("이벤트 태스크 재처리를 요청할 수 있다.")
	void retry() {
		commandEventTaskService.retry(1L);

		verify(eventTaskProcessor).process(1L);
	}
}
