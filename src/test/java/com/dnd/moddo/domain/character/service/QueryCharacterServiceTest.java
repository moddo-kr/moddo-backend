package com.dnd.moddo.character.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.character.service.QueryCharacterService;
import com.dnd.moddo.domain.character.service.implementation.CharacterReader;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.exception.CharacterNotFoundException;

@ExtendWith(MockitoExtension.class)
class QueryCharacterServiceTest {

	@Mock
	private CharacterReader characterReader;

	@InjectMocks
	private QueryCharacterService queryCharacterService;

	private Long groupId;
	private CharacterResponse mockCharacterResponse;

	@BeforeEach
	void setUp() {
		groupId = 1L;
		mockCharacterResponse = new CharacterResponse(
			"러키 모또",
			"1",
			"https://moddo-s3.s3.amazonaws.com/character/lucky-1.png",
			"https://moddo-s3.s3.amazonaws.com/character/lucky-1-big.png"
		);
	}

	@DisplayName("존재하는 groupId에 대한 캐릭터를 조회하면 성공한다.")
	@Test
	void findCharacterByGroupIdSuccess() {
		// given
		when(characterReader.getCharacterByGroupId(groupId)).thenReturn(mockCharacterResponse);

		// when
		CharacterResponse response = queryCharacterService.findCharacterByGroupId(groupId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo(mockCharacterResponse.name());
		assertThat(response.rarity()).isEqualTo(mockCharacterResponse.rarity());
		assertThat(response.imageUrl()).isEqualTo(mockCharacterResponse.imageUrl());
		assertThat(response.imageBigUrl()).isEqualTo(mockCharacterResponse.imageBigUrl());

		verify(characterReader, times(1)).getCharacterByGroupId(groupId);
	}

	@DisplayName("존재하지 않는 groupId로 캐릭터를 조회하면 예외가 발생한다.")
	@Test
	void findCharacterByGroupIdNotFound() {
		// given
		when(characterReader.getCharacterByGroupId(groupId)).thenThrow(new CharacterNotFoundException());

		// when & then
		assertThrows(CharacterNotFoundException.class, () -> queryCharacterService.findCharacterByGroupId(groupId));

		verify(characterReader, times(1)).getCharacterByGroupId(groupId);
	}
}
