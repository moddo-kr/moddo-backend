package com.dnd.moddo.user.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserDeleter {

	private final UserRepository userRepository;

	public void deleteUser(Long userId) {
		User user = userRepository.getById(userId);
		userRepository.delete(user);
	}
}
