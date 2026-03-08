package com.dnd.moddo.user.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.user.application.QueryUserService;
import com.dnd.moddo.user.presentation.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
	private final QueryUserService queryUserService;

	@GetMapping
	public ResponseEntity<UserResponse> getUser(@LoginUser LoginUserInfo loginUser) {
		UserResponse response = queryUserService.findUserById(loginUser.userId());
		return ResponseEntity.ok(response);
	}
}
