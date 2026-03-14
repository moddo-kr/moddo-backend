package com.dnd.moddo.reward.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.reward.application.impl.RewardGrantHandler;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandRewardService {
	private final RewardGrantHandler rewardGrantHandler;

	public void manualGrant(Long settlementId, Long targetUserId) {
		rewardGrantHandler.handle(settlementId, targetUserId);
	}
}
