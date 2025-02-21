package com.dnd.moddo.character.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.character.service.implementation.CharacterReader;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.image.dto.CharacterResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryCharacterService {

	private final GroupReader groupReader;
	private final CharacterReader characterReader;

	public CharacterResponse findCharacterByGroupId(Long groupId) {
		CharacterResponse response = characterReader.getCharacterByGroupId(groupId);
		return response;
	}
}
