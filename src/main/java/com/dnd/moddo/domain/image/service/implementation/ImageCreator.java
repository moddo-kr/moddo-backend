package com.dnd.moddo.domain.image.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dnd.moddo.domain.image.exception.S3SaveException;
import com.dnd.moddo.global.config.S3Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageCreator {
    private final S3Bucket s3Bucket;
    private final AmazonS3 amazonS3;

    public String createTempImage(MultipartFile multipartFile) {
        String fileName = createFileName(multipartFile, "temp");

        try {
            PutObjectRequest request = new PutObjectRequest(
                    s3Bucket.getS3Bucket(),
                    fileName,
                    multipartFile.getInputStream(),
                    getMetadata(multipartFile)
            );
            amazonS3.putObject(request);
        } catch (IOException e) {
            throw new S3SaveException();
        }

        return fileName;
    }

    private String createFileName(MultipartFile multipartFile, String folderPath) {
        return folderPath + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
    }

    private ObjectMetadata getMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        return metadata;
    }
}
