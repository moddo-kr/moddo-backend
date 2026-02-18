package com.dnd.moddo.auth.application;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.auth.model.AuthDetails;
import com.dnd.moddo.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Long userId = parseUserId(username);

		var user = userRepository.findById(userId)
			.orElseThrow(() ->
				new UsernameNotFoundException("User not found: " + username)
			);

		return new AuthDetails(
			user.getId(),
			user.getEmail(),
			user.getAuthority().name()
		);
	}

	private Long parseUserId(String username) {
		try {
			return Long.parseLong(username);
		} catch (NumberFormatException e) {
			throw new UsernameNotFoundException("Invalid user id: " + username);
		}
	}

}
