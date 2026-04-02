package com.dnd.moddo.domain.reward.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.cache.CacheEvictor;
import com.dnd.moddo.reward.application.RewardService;
import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.infrastructure.CollectionRepository;
import com.dnd.moddo.reward.infrastructure.RewardQueryRepository;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

	@Mock
	private RewardQueryRepository rewardQueryRepository;

	@Mock
	private CollectionRepository collectionRepository;
	@Mock
	private CacheEvictor cacheEvictor;

	@InjectMocks
	private RewardService rewardService;

	@Test
	@DisplayName("수동 보상 지급을 위임한다.")
	void manualGrant() {
		RewardService spyService = spy(rewardService);
		doNothing().when(spyService).grant(1L, 2L);

		spyService.manualGrant(1L, 2L);

		verify(spyService).grant(1L, 2L);
	}

	@Test
	@DisplayName("새 보상을 지급하면 컬렉션 캐시를 비운다.")
	void grant_EvictsCollectionsCache_WhenNewCollectionSaved() {
		Character character = mock(Character.class);
		when(character.getId()).thenReturn(10L);
		when(rewardQueryRepository.findBySettlementId(1L)).thenReturn(java.util.Optional.of(character));
		when(collectionRepository.existsByUserIdAndCharacterId(2L, 10L)).thenReturn(false);

		assertThatCode(() -> rewardService.grant(1L, 2L)).doesNotThrowAnyException();

		verify(collectionRepository).save(any());
		verify(cacheEvictor).evictCollections(2L);
	}
}
