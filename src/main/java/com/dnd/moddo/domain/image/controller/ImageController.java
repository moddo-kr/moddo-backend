package com.dnd.moddo.domain.image.controller;

import com.dnd.moddo.domain.image.dto.ImageResponse;
import com.dnd.moddo.domain.image.dto.TempImageResponse;
import com.dnd.moddo.domain.image.service.CommandImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
    private final CommandImageService commandImageService;

    @PostMapping("/temp")
    public ResponseEntity<TempImageResponse> saveTempImage(@RequestParam("file") MultipartFile file) {
        TempImageResponse uniqueKey = commandImageService.uploadTempImage(file);
        return ResponseEntity.ok(uniqueKey);
    }

    @PostMapping("/update")
    public ResponseEntity<ImageResponse> updateImage(@RequestParam("uniqueKey") String uniqueKey) {
        ImageResponse finalImagePath = commandImageService.uploadFinalImage(uniqueKey);
        return ResponseEntity.ok(finalImagePath);
    }
}

