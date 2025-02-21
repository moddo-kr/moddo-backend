package com.dnd.moddo.domain.image.service.implementation;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.entity.type.Characters;
import com.dnd.moddo.domain.image.exception.CharacterNotFoundException;
import com.dnd.moddo.global.config.S3Bucket;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageReader {

	private final S3Bucket s3Bucket;
	private final GroupRepository groupRepository;

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
