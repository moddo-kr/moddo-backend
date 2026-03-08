package com.dnd.moddo.domain.image.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.config.S3Bucket;
import com.dnd.moddo.image.application.impl.ImageReader;
import com.dnd.moddo.image.domain.exception.CharacterNotFoundException;
import com.dnd.moddo.image.presentation.response.CharacterResponse;
import com.dnd.moddo.reward.domain.character.Character;
import com.dnd.moddo.reward.infrastructure.CharacterRepository;

@ExtendWith(MockitoExtension.class)
class ImageReaderTest {

	@Mock
	private S3Bucket s3Bucket;
	@Mock
	private CharacterRepository characterRepository;

	@InjectMocks
	private ImageReader imageReader;

	@DisplayName("랜덤 캐릭터를 성공적으로 조회한다.")
	@Test
	void getRandomCharacterSuccess() {
		// given
		Character character = mock(Character.class);
		when(character.getName()).thenReturn("모또");
		when(character.getRarity()).thenReturn(1);

		when(characterRepository.findByRarity(anyInt())).thenReturn(List.of(character));
		when(s3Bucket.getS3Url()).thenReturn("https://s3.baseUrl.com/");

		// when
		CharacterResponse response = imageReader.getRandomCharacter();

		// then
		assertThat(response).isNotNull();
		assertThat(response.name()).isEqualTo("모또");
		verify(characterRepository, times(1)).findByRarity(anyInt());
	}

	@DisplayName("해당 희귀도의 캐릭터가 존재하지 않으면 예외가 발생한다.")
	@Test
	void getRandomCharacterFail_whenCharacterNotFound() {
		// given
		when(characterRepository.findByRarity(anyInt())).thenReturn(Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> imageReader.getRandomCharacter())
			.isInstanceOf(CharacterNotFoundException.class);
	}
}
