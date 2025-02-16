package com.dnd.moddo.domain.image.service;

import com.dnd.moddo.domain.image.dto.ImageResponse;
import com.dnd.moddo.domain.image.dto.TempImageResponse;
import com.dnd.moddo.domain.image.entity.Image;
import com.dnd.moddo.domain.image.exception.InvalidUniqueKeyException;
import com.dnd.moddo.domain.image.repository.ImageRepository;
import com.dnd.moddo.domain.image.service.implementation.ImageCreator;
import com.dnd.moddo.domain.image.service.implementation.ImageUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandImageService {
    private final ImageCreator imageCreator;
    private final ImageUpdater imageUpdater;
    private final ImageRepository imageRepository;

    public TempImageResponse uploadTempImage(List<MultipartFile> files) {
        List<String> uniqueKeys = files.stream()
                .map(file -> {
                    String uniqueKey = UUID.randomUUID().toString();
                    String tempPath = imageCreator.createTempImage(file);
                    imageRepository.save(new Image(uniqueKey, tempPath));
                    return uniqueKey;
                })
                .toList();

        return TempImageResponse.from(uniqueKeys);
    }

    public ImageResponse uploadFinalImage(List<String> uniqueKeys) {
        List<Image> tempImages = imageRepository.findByUniqueKeyIn(uniqueKeys);

        if (tempImages.size() != uniqueKeys.size()) {
            throw new InvalidUniqueKeyException();
        }

        List<String> finalImagePaths = tempImages.stream()
                .map(tempImage -> {
                    String finalPath = imageUpdater.moveToBucket(tempImage.getPath());
                    imageRepository.delete(tempImage);
                    return finalPath;
                })
                .toList();

        return ImageResponse.from(finalImagePaths);
    }
}
