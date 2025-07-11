package com.dnd.moddo.domain.user.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

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
