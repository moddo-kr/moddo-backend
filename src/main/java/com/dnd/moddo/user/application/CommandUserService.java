package com.dnd.moddo.user.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.user.application.impl.UserCreator;
import com.dnd.moddo.user.application.impl.UserReader;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.presentation.request.GuestUserSaveRequest;
import com.dnd.moddo.user.presentation.request.UserSaveRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandUserService {
	private final UserCreator userCreator;
	private final UserReader userReader;

	@Transactional
	public User createGuestUser(GuestUserSaveRequest request) {
		return userCreator.createUser(request.toEntity());
	}

	@Transactional
	public User createKakaoUser(UserSaveRequest request) {
		return userCreator.createUser(request.toEntity());
	}

	@Transactional
	public User getOrCreateUser(UserSaveRequest request) {
		return userReader.findByKakaoId(request.kakaoId())
			.orElseGet(() -> createKakaoUser(request));
	}
}
