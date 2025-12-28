package com.dnd.moddo.integration;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.global.support.GroupTestFactory;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@EnableCaching
@Transactional
public class CacheIntegrationTest {

	@Autowired
	private QuerySettlementService querySettlementService;

	@Autowired
	private SettlementRepository settlementRepository;

	@MockBean
	private SettlementReader settlementReader;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private static GenericContainer redis;

	static {
		redis = new GenericContainer("redis:7-alpine")
			.withExposedPorts(6379);

		redis.start();
	}

	@DynamicPropertySource
	private static void registerRedisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", redis::getHost);
		registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379)
			.toString());
	}

	@BeforeEach
	void setUp() {
		settlementRepository.save(GroupTestFactory.createDefault());
	}

	@AfterAll
	static void tearDown() {
		redis.close();
	}

	@DisplayName("groupCode로 groupId를 조회하면 Redis에 캐싱되고, 같은 코드로 재조회 시 캐시에서 반환한다")
	@Test
	void findIdByCode_whenQueriedTwice_thenUsesCacheAndCallsReaderOnce() {
		//given
		String code = "code";

		when(settlementReader.findIdByGroupCode("code")).thenReturn(1L);
		//when
		Long first = querySettlementService.findIdByCode(code);
		Long second = querySettlementService.findIdByCode(code);

		//then
		assertThat(first).isEqualTo(second);

		String cacheKey = "settlements::" + code;

		Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
		assertThat(cachedValue).isNotNull();

		verify(settlementReader, times(1)).findIdByGroupCode("code");
	}

}
