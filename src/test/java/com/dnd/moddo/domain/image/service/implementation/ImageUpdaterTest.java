package com.dnd.moddo.domain.image.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.services.s3.AmazonS3;
import com.dnd.moddo.common.config.S3Bucket;
import com.dnd.moddo.image.application.impl.ImageUpdater;

@ExtendWith(MockitoExtension.class)
class ImageUpdaterTest {

	@Mock
	private S3Bucket s3Bucket;

	@Mock
	private AmazonS3 amazonS3;

	@InjectMocks
	private ImageUpdater imageUpdater;

	private String tempPath;
	private String permanentPath;

	@BeforeEach
	void setUp() {
		tempPath = "temp/testImage.jpg";
		permanentPath = "permanent/testImage.jpg";

		lenient().when(s3Bucket.getS3Bucket()).thenReturn("test-bucket");
		lenient().when(s3Bucket.getS3Url()).thenReturn("https://s3.amazonaws.com/test-bucket/");
	}

	@DisplayName("파일을 영구 저장소로 이동시키면 올바른 URL을 반환한다.")
	@Test
	void moveToBucket() {
		// when
		String resultUrl = imageUpdater.moveToBucket(tempPath);

		// then
		verify(amazonS3, times(1)).copyObject(eq("test-bucket"), eq(tempPath), eq("test-bucket"), eq(permanentPath));
		verify(amazonS3, times(1)).deleteObject(eq("test-bucket"), eq(tempPath));
		assertThat(resultUrl).isEqualTo("https://s3.amazonaws.com/test-bucket/permanent/testImage.jpg");
	}

	@DisplayName("파일 경로가 잘못되었을 경우 S3에서 객체 이동을 시도하고 예외가 발생한다.")
	@Test
	void moveToBucket_exception() {
		// given
		doThrow(new RuntimeException("S3 copy failed")).when(amazonS3).copyObject(any(), any(), any(), any());

		// when & then
		try {
			imageUpdater.moveToBucket(tempPath);
		} catch (RuntimeException e) {
			assertThat(e).hasMessageContaining("S3 copy failed");
		}
	}
}
