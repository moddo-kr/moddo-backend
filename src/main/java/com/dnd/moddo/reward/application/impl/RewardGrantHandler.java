package com.dnd.moddo.reward.application.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.domain.character.Collection;
import com.dnd.moddo.reward.domain.character.exception.SettlementCharacterNotFoundException;
import com.dnd.moddo.reward.infrastructure.CollectionRepository;
import com.dnd.moddo.reward.infrastructure.RewardQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RewardGrantHandler {
	private final RewardQueryRepository rewardQueryRepository;
	private final CollectionRepository collectionRepository;

	@Transactional
	public void handle(Long settlementId, Long targetUserId) {
		Character character = rewardQueryRepository.findBySettlementId(settlementId)
			.orElseThrow(() -> new SettlementCharacterNotFoundException(settlementId));

		if (collectionRepository.existsByUserIdAndCharacterId(targetUserId, character.getId())) {
			return;
		}

		try {
			collectionRepository.save(Collection.acquire(targetUserId, character.getId()));
		} catch (DataIntegrityViolationException exception) {
			// Concurrent/manual duplicate grants are treated as idempotent success.
		}
	}
}
