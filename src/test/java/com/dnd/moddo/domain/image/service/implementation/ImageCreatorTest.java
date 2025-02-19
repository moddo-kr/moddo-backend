package com.dnd.moddo.domain.image.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dnd.moddo.domain.image.exception.S3SaveException;
import com.dnd.moddo.global.config.S3Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ImageCreatorTest {

    @Mock
    private S3Bucket s3Bucket;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageCreator imageCreator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("파일 업로드 성공 시 파일 이름을 반환한다.")
    void createTempImage() throws IOException {
        // given
        String originalFileName = "testImage.jpg";
        InputStream inputStream = mock(InputStream.class);
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentLength(100L);
        metadata.setContentType("image/jpeg");

        when(multipartFile.getOriginalFilename()).thenReturn(originalFileName);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getSize()).thenReturn(100L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(s3Bucket.getS3Bucket()).thenReturn("test-bucket");

        // when
        String fileName = imageCreator.createTempImage(multipartFile);

        // then
        verify(amazonS3).putObject(any(PutObjectRequest.class));
        assertThat(fileName).startsWith("temp/");
        assertThat(fileName).endsWith(originalFileName);
    }

    @Test
    @DisplayName("IOException 발생 시 S3SaveException을 던진다.")
    void createTempImage_WhenIOExceptionOccurs() throws IOException {
        // given
        when(multipartFile.getOriginalFilename()).thenReturn("testImage.jpg");
        when(multipartFile.getInputStream()).thenThrow(new IOException("S3 upload failed"));

        // when & then
        try {
            imageCreator.createTempImage(multipartFile);
        } catch (S3SaveException e) {
            assertThat(e).isInstanceOf(S3SaveException.class);
        }
    }
}
