package com.dnd.moddo.reward.application;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.dnd.moddo.common.cache.CacheExecutor;
import com.dnd.moddo.common.cache.CacheKeys;
import com.dnd.moddo.reward.application.impl.CollectionReader;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryCollectionService {
	private static final Duration COLLECTIONS_CACHE_TTL = Duration.ofMinutes(5);

	private final CollectionReader collectionReader;
	private final CacheExecutor cacheExecutor;

	public CollectionListResponse findCollectionListByUserId(Long userId) {
		return cacheExecutor.execute(
			CacheKeys.collections(userId),
			COLLECTIONS_CACHE_TTL,
			() -> collectionReader.findCollectionListByUserId(userId)
		);
	}
}
