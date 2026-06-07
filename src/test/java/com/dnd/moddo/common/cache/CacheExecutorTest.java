package com.dnd.moddo.common.cache;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@ExtendWith(MockitoExtension.class)
class CacheExecutorTest {
	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	private Cache<String, Object> localCache;
	private CacheExecutor cacheExecutor;

	@BeforeEach
	void setUp() {
		ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		localCache = Caffeine.newBuilder().build();
		cacheExecutor = new CacheExecutor(redisTemplate, localCache, objectMapper);
	}

	@Test
	void givenRedisValueIsLinkedHashMap_thenConvertToResponseType() {
		// given
		String key = "settlement-header::1";
		LocalDateTime deadline = LocalDateTime.of(2026, 6, 7, 12, 0);
		LocalDateTime createdAt = LocalDateTime.of(2026, 6, 1, 12, 0);
		Map<String, Object> cachedValue = new LinkedHashMap<>();
		cachedValue.put("groupName", "모또 정산");
		cachedValue.put("totalAmount", 10000L);
		cachedValue.put("deadline", deadline.toString());
		cachedValue.put("bank", "카카오뱅크");
		cachedValue.put("accountNumber", "1234");
		cachedValue.put("createdAt", createdAt.toString());
		cachedValue.put("completedAt", null);

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.get(key)).willReturn(cachedValue);

		// when
		SettlementHeaderResponse response = cacheExecutor.execute(
			key,
			Duration.ofMinutes(5),
			SettlementHeaderResponse.class,
			() -> {
				throw new AssertionError("DB fallback must not be called");
			}
		);

		// then
		assertThat(response.groupName()).isEqualTo("모또 정산");
		assertThat(response.totalAmount()).isEqualTo(10000L);
		assertThat(response.deadline()).isEqualTo(deadline);
		assertThat(response.bank()).isEqualTo("카카오뱅크");
		assertThat(response.accountNumber()).isEqualTo("1234");
		assertThat(response.createdAt()).isEqualTo(createdAt);
		assertThat(response.completedAt()).isNull();
		then(valueOperations).should(never()).set(anyString(), any(), any(Duration.class));
	}

	@Test
	void givenRedisValueIsMissing_thenSaveDbFallbackValue() {
		// given
		String key = "settlement-header::1";
		SettlementHeaderResponse fallback = new SettlementHeaderResponse(
			"모또 정산",
			10000L,
			LocalDateTime.of(2026, 6, 7, 12, 0),
			"카카오뱅크",
			"1234",
			LocalDateTime.of(2026, 6, 1, 12, 0),
			null
		);

		given(redisTemplate.opsForValue()).willReturn(valueOperations);
		given(valueOperations.get(key)).willReturn(null);

		// when
		SettlementHeaderResponse response = cacheExecutor.execute(
			key,
			Duration.ofMinutes(5),
			SettlementHeaderResponse.class,
			() -> fallback
		);

		// then
		assertThat(response).isEqualTo(fallback);
		then(valueOperations).should(times(1)).set(eq(key), eq(fallback), eq(Duration.ofMinutes(5)));
	}
}
