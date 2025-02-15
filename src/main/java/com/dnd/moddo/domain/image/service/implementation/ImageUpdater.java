package com.dnd.moddo.domain.image.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.dnd.moddo.global.config.S3Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageUpdater {
    private final S3Bucket s3Bucket;
    private final AmazonS3 amazonS3;

    public String moveToPermanentStorage(String tempPath) {
        String finalPath = tempPath.replace("temp/", "permanent/");

        amazonS3.copyObject(s3Bucket.getS3Bucket(), tempPath, s3Bucket.getS3Bucket(), finalPath);
        amazonS3.deleteObject(s3Bucket.getS3Bucket(), tempPath);

        return s3Bucket.getS3Url() + finalPath;
    }
}