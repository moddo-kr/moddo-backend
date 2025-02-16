package com.dnd.moddo.domain.image.service;

import com.dnd.moddo.domain.image.dto.ImageResponse;
import com.dnd.moddo.domain.image.entity.Image;
import com.dnd.moddo.domain.image.exception.InvalidUniqueKeyException;
import com.dnd.moddo.domain.image.repository.ImageRepository;
import com.dnd.moddo.domain.image.service.implementation.ImageUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class CommandImageServiceTest {

    @Mock
    private ImageUpdater imageUpdater;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private CommandImageService commandImageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이미지 최종화 성공 시 이미지 경로 반환")
    void finalizeImage_Success() {
        // given
        String uniqueKey = "uniqueKey";
        String tempPath = "temp/tempImage.jpg";
        String finalPath = "permanent/finalImage.jpg";
        Image tempImage = new Image(uniqueKey, tempPath);

        when(imageRepository.findByUniqueKey(uniqueKey)).thenReturn(Optional.of(tempImage));
        when(imageUpdater.moveToBucket(tempPath)).thenReturn(finalPath);

        // when
        ImageResponse response = commandImageService.uploadFinalImage(uniqueKey);

        // then
        assertThat(response.path()).isEqualTo(finalPath);
        verify(imageRepository, times(1)).delete(tempImage);
    }


    @Test
    @DisplayName("잘못된 uniqueKey로 이미지 최종화 시 InvalidUniqueKeyException 발생")
    void finalizeImage_InvalidKey() {
        // given
        String uniqueKey = "invalidKey";

        when(imageRepository.findByUniqueKey(uniqueKey)).thenReturn(Optional.empty());

        // when & then
        try {
            commandImageService.uploadFinalImage(uniqueKey);
        } catch (InvalidUniqueKeyException e) {
            assertThat(e).isInstanceOf(InvalidUniqueKeyException.class);
        }
    }
}
