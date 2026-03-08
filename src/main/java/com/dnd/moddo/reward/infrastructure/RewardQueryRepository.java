package com.dnd.moddo.reward.infrastructure;

import java.util.Optional;

import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.presentation.response.CollectionListResponse;

public interface RewardQueryRepository {
	CollectionListResponse getCollectionListByUserId(Long userId);

	Optional<Character> findBySettlementId(Long settlementId);
}
