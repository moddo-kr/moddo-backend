package com.dnd.moddo.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.dnd.moddo.auth.application.RefreshTokenBlacklist;
import com.dnd.moddo.auth.infrastructure.security.JwtProvider;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class RefreshTokenBlacklistTest {
	private static final String KEY_PREFIX = "auth:refresh:blacklist:";

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private ObjectProvider<RedisTemplate<String, Object>> redisTemplateProvider;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	@Mock
	private Claims claims;

	@Test
	void givenBlankRefreshToken_thenDoNothing() {
		// given
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);

		// when
		blacklist.revoke(" ");

		// then
		then(jwtProvider).should(never()).parseClaims(anyString());
		then(redisTemplateProvider).should(never()).getIfAvailable();
		assertThat(blacklist.isRevoked(" ")).isFalse();
	}

	@Test
	void givenRefreshTokenWithFutureExpiration_thenSaveHashedKeyToRedisAndMarkRevoked() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(jwtProvider.parseClaims(refreshToken)).willReturn(claims);
		given(claims.getExpiration()).willReturn(Date.from(Instant.now().plusSeconds(60)));
		given(redisTemplateProvider.getIfAvailable()).willReturn(redisTemplate);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);

		// when
		blacklist.revoke(refreshToken);

		// then
		ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
		then(valueOperations).should(times(1)).set(keyCaptor.capture(), eq(true), ttlCaptor.capture());
		assertThat(keyCaptor.getValue()).startsWith(KEY_PREFIX);
		assertThat(keyCaptor.getValue()).doesNotContain(refreshToken);
		assertThat(ttlCaptor.getValue()).isPositive();
		assertThat(blacklist.isRevoked(refreshToken)).isTrue();
	}

	@Test
	void givenRefreshTokenWithoutExpiration_thenDoNotSaveToRedis() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(jwtProvider.parseClaims(refreshToken)).willReturn(claims);
		given(claims.getExpiration()).willReturn(null);

		// when
		blacklist.revoke(refreshToken);

		// then
		then(redisTemplateProvider).should(never()).getIfAvailable();
		assertThat(blacklist.isRevoked(refreshToken)).isFalse();
	}

	@Test
	void givenExpiredRefreshToken_thenDoNotSaveToRedis() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(jwtProvider.parseClaims(refreshToken)).willReturn(claims);
		given(claims.getExpiration()).willReturn(Date.from(Instant.now().minusSeconds(1)));

		// when
		blacklist.revoke(refreshToken);

		// then
		then(redisTemplateProvider).should(never()).getIfAvailable();
		assertThat(blacklist.isRevoked(refreshToken)).isFalse();
	}

	@Test
	void givenRedisTemplateIsMissing_whenRevoke_thenUseLocalBlacklist() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(jwtProvider.parseClaims(refreshToken)).willReturn(claims);
		given(claims.getExpiration()).willReturn(Date.from(Instant.now().plusSeconds(60)));
		given(redisTemplateProvider.getIfAvailable()).willReturn(null);

		// when
		blacklist.revoke(refreshToken);

		// then
		assertThat(blacklist.isRevoked(refreshToken)).isTrue();
	}

	@Test
	void givenRedisWriteFails_whenRevoke_thenUseLocalBlacklist() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(jwtProvider.parseClaims(refreshToken)).willReturn(claims);
		given(claims.getExpiration()).willReturn(Date.from(Instant.now().plusSeconds(60)));
		given(redisTemplateProvider.getIfAvailable()).willReturn(redisTemplate);
		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		willThrow(new RuntimeException("redis down"))
			.given(valueOperations).set(anyString(), eq(true), any(Duration.class));

		// when
		blacklist.revoke(refreshToken);

		// then
		assertThat(blacklist.isRevoked(refreshToken)).isTrue();
	}

	@Test
	void givenRedisHasBlacklistKey_thenReturnRevoked() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(redisTemplateProvider.getIfAvailable()).willReturn(redisTemplate);
		given(redisTemplate.hasKey(argThat(key -> key.startsWith(KEY_PREFIX)))).willReturn(true);

		// when
		boolean revoked = blacklist.isRevoked(refreshToken);

		// then
		assertThat(revoked).isTrue();
	}

	@Test
	void givenRedisDoesNotHaveBlacklistKey_thenReturnNotRevoked() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(redisTemplateProvider.getIfAvailable()).willReturn(redisTemplate);
		given(redisTemplate.hasKey(argThat(key -> key.startsWith(KEY_PREFIX)))).willReturn(false);

		// when
		boolean revoked = blacklist.isRevoked(refreshToken);

		// then
		assertThat(revoked).isFalse();
	}

	@Test
	void givenRedisReadFails_thenReturnNotRevoked() {
		// given
		String refreshToken = "refresh-token";
		RefreshTokenBlacklist blacklist = new RefreshTokenBlacklist(jwtProvider, redisTemplateProvider);
		given(redisTemplateProvider.getIfAvailable()).willReturn(redisTemplate);
		given(redisTemplate.hasKey(anyString())).willThrow(new RuntimeException("redis down"));

		// when
		boolean revoked = blacklist.isRevoked(refreshToken);

		// then
		assertThat(revoked).isFalse();
	}
}
