package com.dnd.moddo.domain.reward.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.reward.application.RewardService;
import com.dnd.moddo.reward.infrastructure.CollectionRepository;
import com.dnd.moddo.reward.infrastructure.RewardQueryRepository;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

	@Mock
	private RewardQueryRepository rewardQueryRepository;

	@Mock
	private CollectionRepository collectionRepository;

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
}
