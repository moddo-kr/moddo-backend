package com.dnd.moddo.domain.image.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.entity.type.Character;
import com.dnd.moddo.domain.image.service.implementation.ImageReader;

class QueryImageServiceTest {

	@Mock
	private ImageReader imageReader;

	@InjectMocks
	private QueryImageService queryImageService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getCharacter() {
		// given
		Character character = Character.ANGEL;
		CharacterResponse Response = new CharacterResponse(character.getName(), String.valueOf(character.getRarity()),
			"imgUrl", "bigImgUrl");

		// when
		when(imageReader.getRandomCharacter(1L)).thenReturn(Response);
		CharacterResponse result = queryImageService.getCharacter(1L);

		// then
		assertThat(result.rarity()).isEqualTo(String.valueOf(character.getRarity()));
		assertThat(result.name()).isEqualTo(character.getName());

		// verify
		verify(imageReader, times(1)).getRandomCharacter(1L);
	}
}
