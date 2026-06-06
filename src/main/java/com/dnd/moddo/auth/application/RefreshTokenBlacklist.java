package com.dnd.moddo.auth.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.dnd.moddo.auth.infrastructure.security.JwtProvider;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenBlacklist {
	private static final String KEY_PREFIX = "auth:refresh:blacklist:";

	private final JwtProvider jwtProvider;
	private final ObjectProvider<RedisTemplate<String, Object>> redisTemplateProvider;
	private final Map<String, Instant> localBlacklist = new ConcurrentHashMap<>();

	public void revoke(String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			return;
		}

		try {
			Claims claims = jwtProvider.parseClaims(refreshToken);
			Date expiration = claims.getExpiration();
			if (expiration == null) {
				return;
			}

			Instant expiresAt = expiration.toInstant();
			Duration ttl = Duration.between(Instant.now(), expiresAt);
			if (ttl.isNegative() || ttl.isZero()) {
				return;
			}

			String key = key(refreshToken);
			localBlacklist.put(key, expiresAt);
			saveToRedis(key, ttl);
		} catch (Exception exception) {
			log.warn("Failed to revoke refresh token", exception);
		}
	}

	public boolean isRevoked(String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			return false;
		}

		String key = key(refreshToken);
		if (isRevokedInLocal(key)) {
			return true;
		}

		try {
			RedisTemplate<String, Object> redisTemplate = redisTemplateProvider.getIfAvailable();
			if (redisTemplate == null) {
				return false;
			}
			return Boolean.TRUE.equals(redisTemplate.hasKey(key));
		} catch (Exception exception) {
			log.warn("Redis blacklist read failed for key={}", key, exception);
			return false;
		}
	}

	private void saveToRedis(String key, Duration ttl) {
		try {
			RedisTemplate<String, Object> redisTemplate = redisTemplateProvider.getIfAvailable();
			if (redisTemplate != null) {
				redisTemplate.opsForValue().set(key, true, ttl);
			}
		} catch (Exception exception) {
			log.warn("Redis blacklist write failed for key={}", key, exception);
		}
	}

	private boolean isRevokedInLocal(String key) {
		Instant expiresAt = localBlacklist.get(key);
		if (expiresAt == null) {
			return false;
		}
		if (Instant.now().isAfter(expiresAt)) {
			localBlacklist.remove(key);
			return false;
		}
		return true;
	}

	private String key(String refreshToken) {
		return KEY_PREFIX + hash(refreshToken);
	}

	private String hash(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
		} catch (NoSuchAlgorithmException exception) {
			throw new IllegalStateException("SHA-256 algorithm is not available", exception);
		}
	}
}
