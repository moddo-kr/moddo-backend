package com.dnd.moddo.outbox.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.model.exception.UserPermissionException;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.outbox.application.command.CommandEventTaskService;
import com.dnd.moddo.user.domain.Authority;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/event-tasks")
public class EventTaskAdminController {
	private final CommandEventTaskService commandEventTaskService;

	@PostMapping("/{eventTaskId}/retry")
	public ResponseEntity<Void> retry(
		@PathVariable Long eventTaskId,
		@LoginUser LoginUserInfo loginUser
	) {
		validateAdmin(loginUser);
		commandEventTaskService.retry(eventTaskId);
		return ResponseEntity.ok().build();
	}

	private void validateAdmin(LoginUserInfo loginUser) {
		if (!Authority.ADMIN.name().equals(loginUser.role())) {
			throw new UserPermissionException();
		}
	}
}
