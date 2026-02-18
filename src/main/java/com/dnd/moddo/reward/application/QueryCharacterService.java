package com.dnd.moddo.reward.application;

import org.springframework.stereotype.Service;

import com.dnd.moddo.image.presentation.response.CharacterResponse;
import com.dnd.moddo.reward.application.impl.CharacterReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryCharacterService {

	private final CharacterReader characterReader;

	public CharacterResponse findCharacterByGroupId(Long groupId) {
		CharacterResponse response = characterReader.getCharacterByGroupId(groupId);
		return response;
	}
}
