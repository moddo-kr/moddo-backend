package com.dnd.moddo.domain.reward.service.implementation;

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

import com.dnd.moddo.reward.application.impl.CollectionReader;
import com.dnd.moddo.reward.infrastructure.RewardQueryRepository;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;
import com.dnd.moddo.reward.presentation.response.CollectionResponse;

@ExtendWith(MockitoExtension.class)
public class CollectionReaderTest {

	@Mock
	private RewardQueryRepository rewardQueryRepository;

	@InjectMocks
	private CollectionReader collectionReader;

	@Test
	@DisplayName("userId로 컬렉션 목록을 성공적으로 조회한다.")
	void getCollectionListByUserId() {
		// given
		Long userId = 1L;
		CollectionListResponse expectedResponse = new CollectionListResponse(List.of(
			new CollectionResponse(1L, "모또", 1, LocalDateTime.now(), "imageUrl", "imageBigUrl")
		));
		when(rewardQueryRepository.getCollectionListByUserId(userId)).thenReturn(expectedResponse);

		// when
		CollectionListResponse result = collectionReader.findCollectionListByUserId(userId);

		// then
		assertThat(result).isEqualTo(expectedResponse);
		verify(rewardQueryRepository, times(1)).getCollectionListByUserId(userId);
	}
}
