package com.dnd.moddo.reward.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.reward.infrastructure.RewardQueryRepository;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionReader {
	private final RewardQueryRepository rewardQueryRepository;

	@Transactional(readOnly = true)
	public CollectionListResponse findCollectionListByUserId(Long userId) {
		return rewardQueryRepository.getCollectionListByUserId(userId);
	}
}
