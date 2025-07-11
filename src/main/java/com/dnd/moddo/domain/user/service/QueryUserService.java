package com.dnd.moddo.domain.user.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.user.service.implementation.UserReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryUserService {
	private final UserReader userReader;

	public Long findKakaoIdById(Long userId) {
		return userReader.findKakaoIdById(userId)
			.orElse(null);
	}
}

