package com.dnd.moddo.domain.image.service.implementation;

import com.dnd.moddo.domain.image.dto.CharacterResponse;
import com.dnd.moddo.domain.image.entity.type.Character;
import com.dnd.moddo.domain.image.exception.CharacterNotFoundException;
import com.dnd.moddo.global.config.S3Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ImageReader {

    private final S3Bucket s3Bucket;

    public CharacterResponse getRandomCharacter() {
        int rarity = getRandomRarity();
        List<Character> characters = Character.getByRarity(rarity);

        if (characters.isEmpty()) {
            throw new CharacterNotFoundException();
        }

        Character character = characters.get(new Random().nextInt(characters.size()));

        return CharacterResponse.of(character.getName(), String.valueOf(rarity), getImageUrl(character.getName(), rarity), getBigImageUrl(character.getName(), rarity));
    }

    private int getRandomRarity() {
        int roll = new Random().nextInt(100);
        if (roll < 60) return 1;
        if (roll < 90) return 2;
        return 3;
    }

    private String getImageUrl(String characterName, int rarity) {
        return s3Bucket.getS3Url() + "character/" + characterName + "-" + rarity + ".png";
    }

    private String getBigImageUrl(String characterName, int rarity) {
        return s3Bucket.getS3Url() + "character/" + characterName + "-" + rarity + "-big.png";
    }
}
