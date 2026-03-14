package com.dnd.moddo.domain.outbox.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.dnd.moddo.auth.model.exception.UserPermissionException;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.outbox.application.CommandEventTaskService;
import com.dnd.moddo.outbox.presentation.EventTaskAdminController;

@ExtendWith(MockitoExtension.class)
class EventTaskAdminControllerTest {

	@Mock
	private CommandEventTaskService commandEventTaskService;

	@InjectMocks
	private EventTaskAdminController eventTaskAdminController;

	@Test
	@DisplayName("관리자는 이벤트 태스크 재시도를 요청할 수 있다.")
	void retryByAdmin() {
		ResponseEntity<Void> response = eventTaskAdminController.retry(1L, new LoginUserInfo(1L, "ADMIN"));

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		verify(commandEventTaskService).retry(1L);
	}

	@Test
	@DisplayName("관리자가 아니면 이벤트 태스크 재시도를 요청할 수 없다.")
	void retryForbiddenWhenNotAdmin() {
		assertThatThrownBy(() -> eventTaskAdminController.retry(1L, new LoginUserInfo(1L, "USER")))
			.isInstanceOf(UserPermissionException.class);

		verify(eventTaskService, never()).retry(anyLong());
	}
}
