package com.dnd.moddo.domain.image.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.dto.ImageResponse;
import com.dnd.moddo.domain.image.dto.TempImageResponse;
import com.dnd.moddo.domain.image.service.CommandImageService;
import com.dnd.moddo.domain.image.service.QueryImageService;
import com.dnd.moddo.global.jwt.service.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
	private final CommandImageService commandImageService;

	private final QueryImageService queryImageService;
	private final JwtService jwtService;

	@PostMapping("/temp")
	public ResponseEntity<TempImageResponse> saveTempImage(@RequestParam("file") List<MultipartFile> files) {
		TempImageResponse uniqueKey = commandImageService.uploadTempImage(files);
		return ResponseEntity.ok(uniqueKey);
	}

	@PostMapping("/update")
	public ResponseEntity<ImageResponse> updateImage(@RequestParam("uniqueKey") List<String> uniqueKeys) {
		ImageResponse finalImagePath = commandImageService.uploadFinalImage(uniqueKeys);
		return ResponseEntity.ok(finalImagePath);
	}

	@GetMapping("/character")
	public ResponseEntity<CharacterResponse> getRandomCharacter(@RequestParam("groupToken") String groupToken) {
		Long groupId = jwtService.getGroupId(groupToken);
		CharacterResponse character = queryImageService.getCharacter(groupId);
		return ResponseEntity.ok(character);
	}
}

