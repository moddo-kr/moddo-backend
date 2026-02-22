package com.dnd.moddo.auth.infrastructure.security;

import static com.dnd.moddo.auth.infrastructure.security.JwtConstants.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.dnd.moddo.auth.presentation.response.TokenResponse;
import com.dnd.moddo.user.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtProvider {

	private final JwtProperties jwtProperties;

	public String generateAccessToken(Long id, String role) {
		return generateToken(id, role, ACCESS_KEY.getMessage(), jwtProperties.getAccessExpiration());
	}

	public TokenResponse generateToken(User user) {
		return generateToken(
			user.getId(),
			user.getAuthority().toString(),
			user.getIsMember()
		);
	}

	public TokenResponse generateToken(Long id, String role, Boolean isMember) {
		String accessToken = generateToken(
			id, role,
			ACCESS_KEY.getMessage(),
			jwtProperties.getAccessExpiration()
		);

		String refreshToken = generateToken(
			id, role,
			REFRESH_KEY.getMessage(),
			jwtProperties.getRefreshExpiration()
		);

		return new TokenResponse(
			accessToken,
			refreshToken,
			getExpiredTime(),
			isMember
		);
	}

	public String generateGroupToken(Long groupId) {
		return generateGroupToken(groupId, GROUP_KEY.getMessage());
	}

	private String generateToken(Long id, String role, String type, Long exp) {
		return Jwts.builder()
			.claim(AUTH_ID.getMessage(), id)
			.setHeaderParam(TYPE.message, type)
			.claim(ROLE.getMessage(), role)
			.signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
			.setExpiration(new Date(System.currentTimeMillis() + exp * 1000))
			.compact();
	}

	private String generateGroupToken(Long groupId, String type) {
		return Jwts.builder()
			.claim(GROUP_ID.getMessage(), groupId)
			.setHeaderParam(TYPE.message, type)
			.signWith(jwtProperties.getSecretKey(), SignatureAlgorithm.HS256)
			.setExpiration(
				Date.from(
					LocalDate.now()
						.plusMonths(1)
						.atStartOfDay(ZoneId.systemDefault())
						.toInstant()
				)
			)
			.compact();
	}

	private ZonedDateTime getExpiredTime() {
		return ZonedDateTime.now()
			.plusSeconds(jwtProperties.getRefreshExpiration());
	}

	/**
	 * 토큰 유효성 검증
	 */
	public void validate(String token) {
		try {
			parseClaims(token);
		} catch (ExpiredJwtException e) {
			throw e;
		} catch (SecurityException | MalformedJwtException |
				 UnsupportedJwtException | IllegalArgumentException e) {
			throw new JwtException("Invalid JWT token");
		}
	}

	/**
	 * userId 추출
	 */
	public Long getUserId(String token) {
		Claims claims = parseClaims(token);
		return claims.get(AUTH_ID.getMessage(), Long.class);
	}

	/**
	 * role 추출
	 */
	public String getRole(String token) {
		Claims claims = parseClaims(token);
		return claims.get(ROLE.getMessage(), String.class);
	}

	/**
	 * 토큰 타입 추출 (access / refresh 구분용)
	 */
	public String getTokenType(String token) {
		return (String)parseHeader(token).get(TYPE.message);
	}

	public Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(jwtProperties.getSecretKey())
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	private Header<?> parseHeader(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(jwtProperties.getSecretKey())
			.build()
			.parseClaimsJws(token)
			.getHeader();
	}
}