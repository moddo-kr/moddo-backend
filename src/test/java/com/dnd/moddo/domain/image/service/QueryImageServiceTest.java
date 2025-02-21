package com.dnd.moddo.domain.image.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.entity.type.Characters;
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
		Characters characters = Characters.ANGEL;
		CharacterResponse Response = new CharacterResponse(characters.getName(), String.valueOf(characters.getRarity()),
			"imgUrl", "bigImgUrl");

		// when
		when(imageReader.getRandomCharacter()).thenReturn(Response);
		CharacterResponse result = queryImageService.getCharacter();

		// then
		assertThat(result.rarity()).isEqualTo(String.valueOf(characters.getRarity()));
		assertThat(result.name()).isEqualTo(characters.getName());

		// verify
		verify(imageReader, times(1)).getRandomCharacter();
	}
}
