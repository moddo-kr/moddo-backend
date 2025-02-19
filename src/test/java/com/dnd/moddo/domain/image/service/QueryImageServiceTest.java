package com.dnd.moddo.domain.image.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.service.implementation.ImageReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dnd.moddo.domain.image.entity.type.Character;

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
        CharacterResponse Response = new CharacterResponse(character.getName(), String.valueOf(character.getRarity()), "imgUrl", "bigImgUrl");

        // when
        when(imageReader.getRandomCharacter()).thenReturn(Response);
        CharacterResponse result = queryImageService.getCharacter();

        // then
        assertThat(result.rarity()).isEqualTo(String.valueOf(character.getRarity()));
        assertThat(result.name()).isEqualTo(character.getName());

        // verify
        verify(imageReader, times(1)).getRandomCharacter();
    }
}
