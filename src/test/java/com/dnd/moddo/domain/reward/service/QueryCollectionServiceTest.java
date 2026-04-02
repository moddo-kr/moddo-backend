package com.dnd.moddo.domain.reward.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.cache.CacheExecutor;
import com.dnd.moddo.reward.application.QueryCollectionService;
import com.dnd.moddo.reward.application.impl.CollectionReader;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;
import com.dnd.moddo.reward.presentation.response.CollectionResponse;

@ExtendWith(MockitoExtension.class)
public class QueryCollectionServiceTest {

	@Mock
	private CollectionReader collectionReader;
	@Mock
	private CacheExecutor cacheExecutor;

	@InjectMocks
	private QueryCollectionService queryCollectionService;

	@Test
	@DisplayName("userId로 컬렉션 목록을 성공적으로 조회한다.")
	void findCollectionListByUserId() {
		// given
		Long userId = 1L;
		CollectionListResponse expectedResponse = new CollectionListResponse(List.of(
			new CollectionResponse(1L, "모또", 1, LocalDateTime.now(), "imageUrl", "imageBigUrl")
		));
		when(cacheExecutor.execute(anyString(), any(), any())).thenReturn(expectedResponse);

		// when
		CollectionListResponse result = queryCollectionService.findCollectionListByUserId(userId);

		// then
		assertThat(result).isEqualTo(expectedResponse);
		verify(cacheExecutor, times(1)).execute(anyString(), any(), any());
	}

	@Test
	@DisplayName("컬렉션이 없어도 빈 목록을 정상적으로 반환한다.")
	void findCollectionListByUserId_WhenEmpty() {
		Long userId = 1L;
		CollectionListResponse emptyResponse = new CollectionListResponse(List.of());
		when(cacheExecutor.execute(anyString(), any(), any())).thenReturn(emptyResponse);

		CollectionListResponse result = queryCollectionService.findCollectionListByUserId(userId);

		assertThat(result.collections()).isEmpty();
		verify(cacheExecutor).execute(anyString(), any(), any());
	}

	@Test
	@DisplayName("캐시 조회 중 예외가 발생하면 그대로 전파한다.")
	void findCollectionListByUserId_WhenCacheExecutorFails() {
		Long userId = 1L;
		when(cacheExecutor.execute(anyString(), any(), any()))
			.thenThrow(new RuntimeException("cache failure"));

		assertThatThrownBy(() -> queryCollectionService.findCollectionListByUserId(userId))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("cache failure");

		verify(cacheExecutor).execute(anyString(), any(), any());
	}
}
