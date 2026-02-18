package com.dnd.moddo.reward.application.impl;

import org.springframework.stereotype.Service;

import com.dnd.moddo.image.domain.exception.CharacterNotFoundException;
import com.dnd.moddo.image.presentation.response.CharacterResponse;
import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.infrastructure.CharacterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CharacterReader {
	private final CharacterRepository characterRepository;

	public CharacterResponse getCharacterByGroupId(Long groupId) {
		Character character = characterRepository.findBySettlementId(groupId)
			.orElseThrow(CharacterNotFoundException::new);

		return CharacterResponse.from(character);
	}
}
