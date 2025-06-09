package com.dnd.moddo.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@EnableCaching
@Configuration
@Profile("!test")
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(host);
		config.setPort(port);

		// 비밀번호가 설정되어 있는 경우에만 적용(운영 환경)
		if (!password.isEmpty()) {
			config.setPassword(password);
		}

		return new LettuceConnectionFactory(config);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		return RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10)))
			.build();
	}
}
