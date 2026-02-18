package com.dnd.moddo.image.application.impl;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.dnd.moddo.common.config.S3Bucket;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.image.domain.exception.CharacterNotFoundException;
import com.dnd.moddo.image.domain.type.Characters;
import com.dnd.moddo.image.presentation.response.CharacterResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageReader {

	private final S3Bucket s3Bucket;
	private final SettlementRepository settlementRepository;

	public CharacterResponse getRandomCharacter() {
		int rarity = getRandomRarity();
		List<Characters> characters = Characters.getByRarity(rarity);

		if (characters.isEmpty()) {
			throw new CharacterNotFoundException();
		}

		Characters character = characters.get(new Random().nextInt(characters.size()));
		return CharacterResponse.of(character, s3Bucket);
	}

	private int getRandomRarity() {
		int roll = new Random().nextInt(100);
		if (roll < 60)
			return 1;
		if (roll < 90)
			return 2;
		return 3;
	}
}
