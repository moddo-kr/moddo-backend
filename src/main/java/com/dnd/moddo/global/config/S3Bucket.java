package com.dnd.moddo.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class S3Bucket {
    @Value("${aws.s3.bucket}")
    private String s3Bucket;

    public String getS3Url() {
        return "https://" + s3Bucket + ".s3.amazonaws.com/";
    }
}
