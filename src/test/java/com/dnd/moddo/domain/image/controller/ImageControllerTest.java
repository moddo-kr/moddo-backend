package com.dnd.moddo.domain.image.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.dnd.moddo.domain.image.dto.ImageResponse;
import com.dnd.moddo.domain.image.dto.TempImageResponse;
import com.dnd.moddo.global.util.RestDocsTestSupport;

public class ImageControllerTest extends RestDocsTestSupport {

	@Test
	@DisplayName("임시 이미지를 성공적으로 업로드한다.")
	void saveTempImageSuccess() throws Exception {
		// given
		MockMultipartFile file1 = new MockMultipartFile("file", "image1.jpg", "image/jpeg", "image-data".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("file", "image2.jpg", "image/jpeg", "image-data".getBytes());
		TempImageResponse tempImageResponse = TempImageResponse.from(List.of("key1", "key2"));

		when(commandImageService.uploadTempImage(any())).thenReturn(tempImageResponse);

		// when & then
		mockMvc.perform(multipart("/api/v1/images/temp")
				.file(file1)
				.file(file2))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.uniqueKeys").isArray())
			.andExpect(jsonPath("$.uniqueKeys[0]").value("key1"))
			.andExpect(jsonPath("$.uniqueKeys[1]").value("key2"));
	}

	@Test
	@DisplayName("최종 이미지를 성공적으로 업데이트한다.")
	void updateImageSuccess() throws Exception {
		// given
		ImageResponse imageResponse = ImageResponse.from(
			List.of("https://bucket.s3.amazonaws.com/permanent/image1.jpg"));

		when(commandImageService.uploadFinalImage(anyList())).thenReturn(imageResponse);

		// when & then
		mockMvc.perform(post("/api/v1/images/update")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("uniqueKey", "key1", "key2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.paths").isArray())
			.andExpect(jsonPath("$.paths[0]").value("https://bucket.s3.amazonaws.com/permanent/image1.jpg"));
	}
}
