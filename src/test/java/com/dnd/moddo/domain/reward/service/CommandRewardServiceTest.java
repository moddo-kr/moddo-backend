package com.dnd.moddo.domain.reward.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.reward.application.CommandRewardService;
import com.dnd.moddo.reward.application.impl.RewardGrantHandler;

@ExtendWith(MockitoExtension.class)
class CommandRewardServiceTest {

	@Mock
	private RewardGrantHandler rewardGrantHandler;

	@InjectMocks
	private CommandRewardService commandRewardService;

	@Test
	@DisplayName("수동 보상 지급을 위임한다.")
	void manualGrant() {
		commandRewardService.manualGrant(1L, 2L);

		verify(rewardGrantHandler).handle(1L, 2L);
	}
}
