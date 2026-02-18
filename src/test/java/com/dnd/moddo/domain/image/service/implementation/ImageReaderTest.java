package com.dnd.moddo.domain.image.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.common.config.S3Bucket;
import com.dnd.moddo.image.application.impl.ImageReader;
import com.dnd.moddo.image.domain.type.Characters;
import com.dnd.moddo.image.presentation.response.CharacterResponse;

@ExtendWith(MockitoExtension.class)
class ImageReaderTest {

	@Mock
	private S3Bucket s3Bucket;

	@InjectMocks
	private ImageReader imageReader;

	@DisplayName("Character가 존재할 경우, 랜덤 캐릭터 정보와 이미지 URL을 반환하고," +
		"Character가 존재하지 않으면 CharacterNotFoundException 예외를 발생시킨다.")
	@Test
	void getRandomCharacter() {
		// given
		Characters characters = Characters.LUCKY;
		List<Characters> characterList = List.of(characters);

		try (MockedStatic<Characters> mockedCharacter = mockStatic(Characters.class)) {
			// when
			mockedCharacter.when(() -> Characters.getByRarity(1)).thenReturn(characterList);

			String imageUrl = "https://mock-s3-url.com/images/1/" + characters.getName() + ".png";
			String imageBigUrl = "https://mock-s3-url.com/images/big/1/" + characters.getName() + ".png";
			CharacterResponse response = new CharacterResponse(
				characters.getName(),
				"1",
				imageUrl,
				imageBigUrl
			);

			// then
			assertThat(response).isNotNull();
			assertThat(response.name()).isEqualTo(characters.getName());
			assertThat(response.rarity()).isEqualTo("1");
			assertThat(response.imageUrl()).isEqualTo(imageUrl);
			assertThat(response.imageBigUrl()).isEqualTo(imageBigUrl);
		}
	}
}
