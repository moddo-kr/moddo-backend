package com.dnd.moddo.domain.image.dto;

import com.dnd.moddo.domain.image.entity.type.Character;
import com.dnd.moddo.global.config.S3Bucket;

public record CharacterResponse(
	String name,
	String rarity,
	String imageUrl,
	String imageBigUrl
) {
	public static CharacterResponse of(Character character, S3Bucket s3Bucket) {
		String rarityString = String.valueOf(character.getRarity());
		return new CharacterResponse(
			character.getName(),
			rarityString,
			getImageUrl(s3Bucket, character.getName(), character.getRarity()),
			getBigImageUrl(s3Bucket, character.getName(), character.getRarity())
		);
	}

	private static String getImageUrl(S3Bucket s3Bucket, String name, int rarity) {
		return s3Bucket.getS3Url() + "character/" + name + "-" + rarity + ".png";
	}

	private static String getBigImageUrl(S3Bucket s3Bucket, String name, int rarity) {
		return s3Bucket.getS3Url() + "character/" + name + "-" + rarity + "-big.png";
	}
}
