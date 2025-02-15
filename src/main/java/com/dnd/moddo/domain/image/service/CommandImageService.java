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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandImageService {
    private final ImageCreator imageCreator;
    private final ImageUpdater imageUpdater;
    private final ImageRepository imageRepository;

    public TempImageResponse uploadTempImage(MultipartFile file) {
        String uniqueKey = UUID.randomUUID().toString();
        String tempPath = imageCreator.createTempImage(file);

        imageRepository.save(new Image(uniqueKey, tempPath));

        return TempImageResponse.from(uniqueKey);
    }

    public ImageResponse finalizeImage(String uniqueKey) {
        Image tempImage = imageRepository.findByUniqueKey(uniqueKey)
                .orElseThrow(() -> new InvalidUniqueKeyException());

        String finalImagePath = imageUpdater.moveToPermanentStorage(tempImage.getPath());
        imageRepository.delete(tempImage);

        return ImageResponse.from(finalImagePath);
    }
}
