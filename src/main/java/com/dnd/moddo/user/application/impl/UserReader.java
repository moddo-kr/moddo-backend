package com.dnd.moddo.user.application.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReader {
	private final UserRepository userRepository;

	public Optional<User> findByKakaoId(Long kakaoId) {
		return userRepository.findByKakaoId(kakaoId);
	}

	public Optional<Long> findKakaoIdById(Long userId) {
		return userRepository.findKakaoIdById(userId);
	}
}
