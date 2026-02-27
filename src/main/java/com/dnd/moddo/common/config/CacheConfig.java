package com.dnd.moddo.common.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import lombok.extern.slf4j.Slf4j;

@EnableCaching
@Configuration
@Profile("!test")
@Slf4j
public class CacheConfig implements CachingConfigurer { // CachingConfigurer 인터페이스 구현

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
		if (password != null && !password.isBlank()) {
			config.setPassword(password);
		}
		LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
		factory.afterPropertiesSet();
		factory.setValidateConnection(false);
		return factory;
	}

	@Bean
	@Primary
	public CacheManager cacheManager(RedisConnectionFactory factory) {
		// 1. Redis 캐시 설정
		RedisCacheManager redisCacheManager = RedisCacheManager.builder(factory)
			.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10)))
			.build();

		// 2. Redis 실패 시 바라볼 로컬 캐시 (Fallback)
		ConcurrentMapCacheManager localCacheManager = new ConcurrentMapCacheManager("group", "user", "settlements");

		// 3. 복합 캐시 매니저: 순서대로 캐시 시도
		CompositeCacheManager compositeCacheManager = new CompositeCacheManager(redisCacheManager, localCacheManager);
		compositeCacheManager.setFallbackToNoOpCache(true);
		return compositeCacheManager;
	}

	/**
	 * 실행 중 Redis 사망 시 예외를 가로채서 처리
	 */
	@Override
	public CacheErrorHandler errorHandler() {
		return new CacheErrorHandler() {
			@Override
			public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
				log.error("Redis 연결 실패(GET) - 캐시 없이 진행: {}", exception.getMessage());
			}

			@Override
			public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
				log.error("Redis 연결 실패(PUT) - 캐시 없이 진행: {}", exception.getMessage());
			}

			@Override
			public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
				log.error("Redis 연결 실패(EVICT) - 캐시 없이 진행: {}", exception.getMessage());
			}

			@Override
			public void handleCacheClearError(RuntimeException exception, Cache cache) {
				log.error("Redis 연결 실패(CLEAR) - 캐시 없이 진행: {}", exception.getMessage());
			}
		};
	}
}