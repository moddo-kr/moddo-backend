package com.dnd.moddo.reward.application.impl;

import org.springframework.stereotype.Service;

import com.dnd.moddo.image.domain.exception.CharacterNotFoundException;
import com.dnd.moddo.image.presentation.response.CharacterResponse;
import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.infrastructure.CharacterRepository;
import com.dnd.moddo.reward.infrastructure.RewardQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CharacterReader {
	private final CharacterRepository characterRepository;
	private final RewardQueryRepository rewardQueryRepository;

	public CharacterResponse getCharacterByGroupId(Long groupId) {
		Character character = rewardQueryRepository.findBySettlementId(groupId)
			.orElseThrow(CharacterNotFoundException::new);

		return CharacterResponse.from(character);
	}
}
