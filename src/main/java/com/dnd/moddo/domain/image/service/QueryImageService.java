package com.dnd.moddo.domain.image.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.service.implementation.ImageReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryImageService {

	private final ImageReader imageReader;

	public CharacterResponse getCharacter() {
		return imageReader.getRandomCharacter();
	}
}
