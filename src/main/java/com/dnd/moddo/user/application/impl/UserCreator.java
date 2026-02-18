package com.dnd.moddo.user.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserCreator {

	private final UserRepository userRepository;

	public User createUser(User user) {
		return userRepository.save(user);
	}
}
