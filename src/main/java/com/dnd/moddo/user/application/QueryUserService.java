package com.dnd.moddo.user.application;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dnd.moddo.user.application.impl.UserReader;
import com.dnd.moddo.user.presentation.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryUserService {
	private final UserReader userReader;

	public Optional<Long> findKakaoIdById(Long userId) {
		return userReader.findKakaoIdById(userId);
	}

	public UserResponse findUserById(Long userId) {
		return userReader.findById(userId);
	}
}

