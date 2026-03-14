package com.dnd.moddo.auth.infrastructure.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.dnd.moddo.auth.application.AuthDetailsService;
import com.dnd.moddo.auth.model.AuthDetails;
import com.dnd.moddo.auth.infrastructure.security.exception.MissingTokenException;
import com.dnd.moddo.auth.infrastructure.security.exception.TokenInvalidException;
import com.dnd.moddo.user.domain.Authority;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuth {
	private final JwtUtil jwtUtil;
	private final AuthDetailsService authDetailsService;

	public Authentication getAuthentication(String token, String expectedTokenType) {
		Claims claims = jwtUtil.getJwt(token).getBody();

		if (isNotExpectedToken(token, expectedTokenType)) {
			throw new MissingTokenException();
		}

		Long userId = claims.get(JwtConstants.AUTH_ID.message, Long.class);
		String role = claims.get(JwtConstants.ROLE.message, String.class);

		if (Authority.ADMIN.name().equals(role)) {
			UserDetails adminDetails = new AuthDetails(userId, "admin", role);
			return new UsernamePasswordAuthenticationToken(adminDetails, "", adminDetails.getAuthorities());
		}

		UserDetails userDetails = authDetailsService.loadUserByUsername(
			userId.toString());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	private boolean isNotExpectedToken(String token, String expectedTokenType) {
		if (token == null || token.isEmpty()) {
			throw new TokenInvalidException();
		}

		String role = jwtUtil.getJwt(token).getHeader().get(JwtConstants.TYPE.message).toString();
		return !role.equals(expectedTokenType);
	}
}
