package com.dnd.moddo.domain.auth.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.entity.type.Authority;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	@Transactional
	public TokenResponse createGuestUser() {
		String guestEmail = "guest-" + UUID.randomUUID() + "@guest.com";

		User guestUser = User.builder()
			.email(guestEmail)
			.name("Guest")
			.profile(null)
			.createdAt(LocalDateTime.now())
			.expiredAt(LocalDateTime.now().plusMonths(1))
			.authority(Authority.USER)
			.isMember(false)
			.build();

		userRepository.save(guestUser);

		return jwtProvider.generateToken(guestUser.getId(), guestUser.getEmail(), guestUser.getAuthority().toString(),
			guestUser.getIsMember());
	}
}