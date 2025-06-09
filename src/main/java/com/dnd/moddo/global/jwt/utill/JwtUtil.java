package com.dnd.moddo.global.jwt.utill;

import org.springframework.stereotype.Component;

import com.dnd.moddo.global.jwt.exception.MissingTokenException;
import com.dnd.moddo.global.jwt.exception.TokenInvalidException;
import com.dnd.moddo.global.jwt.properties.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
	private final JwtProperties jwtProperties;

	public JwtUtil(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public String resolveToken(HttpServletRequest request) {
		String bearer = request.getHeader(jwtProperties.getHeader());
		return parseToken(bearer);
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