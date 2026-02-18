package com.dnd.moddo.domain.image.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.moddo.image.application.CommandImageService;
import com.dnd.moddo.image.application.impl.ImageCreator;
import com.dnd.moddo.image.application.impl.ImageUpdater;
import com.dnd.moddo.image.domain.Image;
import com.dnd.moddo.image.domain.exception.InvalidUniqueKeyException;
import com.dnd.moddo.image.infrastructure.ImageRepository;
import com.dnd.moddo.image.presentation.response.ImageResponse;
import com.dnd.moddo.image.presentation.response.TempImageResponse;

class CommandImageServiceTest {

	@Mock
	private ImageCreator imageCreator;

	@Mock
	private ImageUpdater imageUpdater;

	@Mock
	private ImageRepository imageRepository;

	@Mock
	private MultipartFile multipartFile1;

	@Mock
	private MultipartFile multipartFile2;

	@InjectMocks
	private CommandImageService commandImageService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("여러 개의 임시 이미지 업로드 성공 시 uniqueKeys 리스트 반환")
	void uploadTempImages_Success() {
		// given
		String uniqueKey1 = UUID.randomUUID().toString();
		String uniqueKey2 = UUID.randomUUID().toString();
		String tempPath1 = "temp/tempImage1.jpg";
		String tempPath2 = "temp/tempImage2.jpg";

		when(imageCreator.createTempImage(multipartFile1)).thenReturn(tempPath1);
		when(imageCreator.createTempImage(multipartFile2)).thenReturn(tempPath2);

		// when
		TempImageResponse response = commandImageService.uploadTempImage(List.of(multipartFile1, multipartFile2));

		// then
		assertThat(response.uniqueKeys()).hasSize(2);  // 두 개의 uniqueKey 확인
		verify(imageCreator, times(1)).createTempImage(multipartFile1);
		verify(imageCreator, times(1)).createTempImage(multipartFile2);
		verify(imageRepository, times(2)).save(any(Image.class));
	}

	@Test
	@DisplayName("여러 개의 임시 이미지를 최종화 성공 시 최종 이미지 경로 리스트 반환")
	void uploadFinalImages_Success() {
		// given
		String uniqueKey1 = UUID.randomUUID().toString();
		String uniqueKey2 = UUID.randomUUID().toString();
		String tempPath1 = "temp/tempImage1.jpg";
		String tempPath2 = "temp/tempImage2.jpg";
		String finalPath1 = "permanent/finalImage1.jpg";
		String finalPath2 = "permanent/finalImage2.jpg";
		Image tempImage1 = new Image(uniqueKey1, tempPath1);
		Image tempImage2 = new Image(uniqueKey2, tempPath2);

		when(imageRepository.findByUniqueKeyIn(List.of(uniqueKey1, uniqueKey2)))
			.thenReturn(List.of(tempImage1, tempImage2));
		when(imageUpdater.moveToBucket(tempPath1)).thenReturn(finalPath1);
		when(imageUpdater.moveToBucket(tempPath2)).thenReturn(finalPath2);

		// when
		ImageResponse response = commandImageService.uploadFinalImage(List.of(uniqueKey1, uniqueKey2));

		// then
		assertThat(response.paths()).hasSize(2);
		assertThat(response.paths()).containsExactlyInAnyOrder(finalPath1, finalPath2);
		verify(imageRepository, times(2)).delete(any(Image.class));
	}

	@Test
	@DisplayName("잘못된 uniqueKey로 여러 개의 이미지 최종화 시 InvalidUniqueKeyException 발생")
	void finalizeImages_InvalidKey() {
		// given
		String invalidKey1 = "invalidKey1";
		String invalidKey2 = "invalidKey2";

		when(imageRepository.findByUniqueKeyIn(List.of(invalidKey1, invalidKey2)))
			.thenReturn(List.of());

		// when & then
		assertThrows(InvalidUniqueKeyException.class,
			() -> commandImageService.uploadFinalImage(List.of(invalidKey1, invalidKey2)));

		verify(imageRepository, times(1)).findByUniqueKeyIn(List.of(invalidKey1, invalidKey2));
		verifyNoMoreInteractions(imageUpdater, imageRepository);
	}
}
