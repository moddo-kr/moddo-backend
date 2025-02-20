package com.dnd.moddo.global.jwt.utill;

import static com.dnd.moddo.global.jwt.properties.JwtConstants.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.dnd.moddo.global.jwt.dto.TokenResponse;
import com.dnd.moddo.global.jwt.properties.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtProvider {

	private final JwtProperties jwtProperties;

	public String generateAccessToken(Long id, String email, String role) {
		return generateToken(id, email, role, ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
	}

	public TokenResponse generateToken(Long id, String email, String role, Boolean isMember) {
		String accessToken = generateToken(id, email, role, ACCESS_KEY.getMessage(),
			jwtProperties.getAccessExpiration());
		String refreshToken = generateToken(id, email, role, REFRESH_KEY.getMessage(),
			jwtProperties.getRefreshExpiration());

		return new TokenResponse(accessToken, refreshToken, getExpiredTime(), isMember);
	}

	public String generateGroupToken(Long groupId) {
		return generateGroupToken(groupId, GROUP_KEY.getMessage());
	}

	private String generateToken(Long id, String email, String role, String type, Long exp) {
		return Jwts.builder()
			.claim(AUTH_ID.getMessage(), id)
			.claim(EMAIL.getMessage(), email)
			.setHeaderParam(TYPE.message, type)
			.claim(ROLE.getMessage(), role)
			.signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
			.setExpiration(
				new Date(System.currentTimeMillis() + exp * 1000)
			)
			.compact();
	}

	private String generateGroupToken(Long groupId, String type) {
		return Jwts.builder()
			.claim(GROUP_ID.getMessage(), groupId)
			.setHeaderParam(TYPE.message, type)
			.signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
			.setExpiration(
				Date.from(LocalDate.now().plusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
			)
			.compact();
	}

	private ZonedDateTime getExpiredTime() {
		return ZonedDateTime.now().plusSeconds(jwtProperties.getRefreshExpiration());
	}
}