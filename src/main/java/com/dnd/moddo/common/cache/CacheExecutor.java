package com.dnd.moddo.common.cache;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheExecutor {
	private final RedisTemplate<String, Object> redisTemplate;
	private final Cache<String, Object> localCache;

	@SuppressWarnings("unchecked")
	public <T> T execute(String key, Duration ttl, Supplier<T> dbFallback) {
		try {
			T redisValue = (T)redisTemplate.opsForValue().get(key);
			if (redisValue != null) {
				return redisValue;
			}

			T dbValue = dbFallback.get();
			if (dbValue == null) {
				return null;
			}

			try {
				redisTemplate.opsForValue().set(key, dbValue, ttl);
			} catch (Exception exception) {
				log.warn("Redis write failed for key={}, storing fallback value locally", key, exception);
				localCache.put(key, dbValue);
			}

			return dbValue;
		} catch (Exception exception) {
			log.warn("Redis read failed for key={}, switching to local fallback", key, exception);
		}

		T localValue = (T)localCache.getIfPresent(key);
		if (localValue != null) {
			return localValue;
		}

		T dbValue = dbFallback.get();
		if (dbValue != null) {
			localCache.put(key, dbValue);
		}
		return dbValue;
	}

	public void evict(String key) {
		localCache.invalidate(key);

		try {
			redisTemplate.delete(key);
		} catch (Exception exception) {
			log.warn("Redis evict failed for key={}", key, exception);
		}
	}

	public void evictByPrefix(String prefix) {
		localCache.asMap().keySet().removeIf(key -> key.startsWith(prefix));

		try {
			ScanOptions options = ScanOptions.scanOptions()
				.match(prefix + "*")
				.count(100)
				.build();
			Set<String> keys = new HashSet<>();

			try (Cursor<String> cursor = redisTemplate.scan(options)) {
				while (cursor.hasNext()) {
					keys.add(cursor.next());
				}
			}

			if (!keys.isEmpty()) {
				redisTemplate.delete(keys);
			}
		} catch (Exception exception) {
			log.warn("Redis prefix evict failed for prefix={}", prefix, exception);
		}
	}
}
