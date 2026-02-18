package com.dnd.moddo.image.presentation.response;

import com.dnd.moddo.common.config.S3Bucket;
import com.dnd.moddo.image.domain.type.Characters;
import com.dnd.moddo.reward.domain.character.Character;

public record CharacterResponse(
	String name,
	String rarity,
	String imageUrl,
	String imageBigUrl
) {
	public static CharacterResponse from(Character character) {
		return new CharacterResponse(
			character.getName(),
			character.getRarity(),
			character.getImageUrl(),
			character.getImageBigUrl()
		);
	}

	public static CharacterResponse of(Characters characters, S3Bucket s3Bucket) {
		String rarityString = String.valueOf(characters.getRarity());
		return new CharacterResponse(
			characters.getName(),
			rarityString,
			getImageUrl(s3Bucket, characters.getName(), characters.getRarity()),
			getBigImageUrl(s3Bucket, characters.getName(), characters.getRarity())
		);
	}

	private static String getImageUrl(S3Bucket s3Bucket, String name, int rarity) {
		return s3Bucket.getS3Url() + "character/" + name + "-" + rarity + ".png";

	}

	private static String getBigImageUrl(S3Bucket s3Bucket, String name, int rarity) {
		return s3Bucket.getS3Url() + "character/" + name + "-" + rarity + "-big.png";
	}
}
