package com.dnd.moddo.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.user.dto.request.GuestUserSaveRequest;
import com.dnd.moddo.domain.user.dto.request.UserSaveRequest;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.service.implementation.UserCreator;
import com.dnd.moddo.domain.user.service.implementation.UserReader;

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
