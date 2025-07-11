package com.dnd.moddo.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.user.dto.request.GuestUserSaveRequest;
import com.dnd.moddo.domain.user.dto.request.UserSaveRequest;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandUserService {
	private final UserRepository userRepository;

	@Transactional
	public User createGuestUser(GuestUserSaveRequest request) {
		return userRepository.save(request.toEntity());
	}

	@Transactional
	public User createKakaoUser(UserSaveRequest request) {
		return userRepository.save(request.toEntity());
	}

	@Transactional
	public User getOrCreateUser(UserSaveRequest request) {
		return userRepository.findByKakaoId(request.kakaoId())
			.orElseGet(() -> createKakaoUser(request));
	}
}
