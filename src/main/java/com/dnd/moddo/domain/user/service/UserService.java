package com.dnd.moddo.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.user.dto.request.UserCreateRequest;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public User createGuestUser(UserCreateRequest request) {
		return userRepository.save(request.toEntity());
	}

	@Transactional
	public User createKakaoUser(UserCreateRequest request) {
		return userRepository.save(request.toEntity());
	}

	@Transactional
	public User getOrCreateUser(UserCreateRequest request) {
		return userRepository.findByKakaoId(request.kakaoId())
			.orElseGet(() -> createKakaoUser(request));
	}
}
