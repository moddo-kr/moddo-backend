package com.dnd.moddo.domain.character.service.implementation;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.character.entity.Character;
import com.dnd.moddo.domain.character.repository.CharacterRepository;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.exception.CharacterNotFoundException;

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
