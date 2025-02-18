package com.dnd.moddo.domain.image.service.implementation;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.entity.type.Character;
import com.dnd.moddo.domain.image.exception.CharacterNotFoundException;
import com.dnd.moddo.global.config.S3Bucket;

@ExtendWith(MockitoExtension.class)
class ImageReaderTest {

    @Mock
    private S3Bucket s3Bucket;

    @InjectMocks
    private ImageReader imageReader;

    @DisplayName("Character가 존재할 경우, 랜덤 캐릭터 정보와 이미지 URL을 반환한다.")
    @Test
    void getRandomCharacter() {
        // given
        int rarity = 1;
        Character character = Character.LUCKY;
        List<Character> characterList = List.of(character);

        when(s3Bucket.getS3Url()).thenReturn("https://mock-s3-url.com/");

        try (MockedStatic<Character> mockedCharacter = mockStatic(Character.class)) {

            mockedCharacter.when(() -> Character.getByRarity(rarity)).thenReturn(characterList);

            // when
            CharacterResponse response = imageReader.getRandomCharacter();

            // then
            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo(character.getName());
            assertThat(response.rarity()).isEqualTo(String.valueOf(rarity));
            assertThat(response.imageUrl()).contains(character.getName());
            assertThat(response.imageBigUrl()).contains(character.getName());

            verify(s3Bucket, times(2)).getS3Url();
            mockedCharacter.verify(() -> Character.getByRarity(rarity), times(1));
        }
    }

    @DisplayName("Character가 존재하지 않으면 CharacterNotFoundException 예외를 발생시킨다.")
    @Test
    void getRandomCharacter_CharacterNotFoundException() {
        // given
        int rarity = 1;

        try (MockedStatic<Character> mockedCharacter = mockStatic(Character.class)) {
            mockedCharacter.when(() -> Character.getByRarity(rarity)).thenReturn(List.of());

            // when & then
            assertThatThrownBy(() -> imageReader.getRandomCharacter())
                    .isInstanceOf(CharacterNotFoundException.class);

            mockedCharacter.verify(() -> Character.getByRarity(rarity), times(1));
        }
    }
}
