package com.dnd.moddo.global.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.user.exception.UserNotFoundException;
import com.dnd.moddo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		return userRepository.findById(Long.parseLong(id))
			.map(AuthDetails::new)
			.orElseThrow(() -> new UserNotFoundException(id));
	}
}
