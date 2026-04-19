package com.dnd.moddo.auth.infrastructure.security;

import org.springframework.stereotype.Component;

import com.dnd.moddo.auth.infrastructure.security.exception.MissingTokenException;
import com.dnd.moddo.auth.infrastructure.security.exception.TokenInvalidException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
	private final JwtProperties jwtProperties;

	public JwtUtil(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public String resolveToken(HttpServletRequest request) {
		String cookieToken = resolveTokenFromCookie(request);
		if (cookieToken != null) {
			return cookieToken;
		}

		String bearer = request.getHeader(jwtProperties.getHeader());
		return parseToken(bearer);
	}

	private String resolveTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (jwtProperties.getAccessCookieName().equals(cookie.getName())) {
				String value = cookie.getValue();
				if (value == null || value.isBlank()) {
					return null;
				}
				return value;
			}
		}

		return null;
	}

	public String parseToken(String bearer) {
		if (bearer != null && bearer.startsWith(jwtProperties.getPrefix())) {
			return bearer.replace(jwtProperties.getPrefix(), "").trim();
		}
		return null;
	}

	public Jws<Claims> getJwt(String token) {
		if (token == null) {
			throw new TokenInvalidException();
		}

		return Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey()).build().parseClaimsJws(token);
	}

	public Long getIdFromToken(String token, String key) {
		if (token == null) {
			throw new MissingTokenException();
		}
		Claims claims = getJwt(token).getBody();
		return claims.get(key, Long.class);
	}
}
