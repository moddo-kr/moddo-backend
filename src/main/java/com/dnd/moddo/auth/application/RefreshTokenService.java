package com.dnd.moddo.auth.application;

import org.springframework.stereotype.Service;

import com.dnd.moddo.auth.infrastructure.security.JwtConstants;
import com.dnd.moddo.auth.infrastructure.security.JwtProvider;
import com.dnd.moddo.auth.infrastructure.security.JwtUtil;
import com.dnd.moddo.auth.model.exception.TokenInvalidException;
import com.dnd.moddo.auth.presentation.response.RefreshResponse;
import com.dnd.moddo.user.domain.User;
import com.dnd.moddo.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	public RefreshResponse execute(String token) {
		try {
			String tokenType = jwtProvider.getTokenType(token);
			if (!JwtConstants.REFRESH_KEY.message.equals(tokenType)) {
				throw new TokenInvalidException();
			}

			Long userId = jwtUtil.getJwt(token)
				.getBody()
				.get(JwtConstants.AUTH_ID.message, Long.class);

			if (userId == null) {
				throw new TokenInvalidException();
			}

			User user = userRepository.getById(userId);
			String newAccessToken = jwtProvider.generateAccessToken(
				user.getId(),
				user.getAuthority().toString()
			);

			return RefreshResponse.builder()
				.accessToken(newAccessToken)
				.build();
		} catch (Exception e) {
			throw new TokenInvalidException();
		}
	}

}
