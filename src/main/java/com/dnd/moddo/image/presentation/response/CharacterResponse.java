package com.dnd.moddo.image.presentation.response;

import com.dnd.moddo.common.config.S3Bucket;
import com.dnd.moddo.reward.domain.character.Character;

public record CharacterResponse(
	Long id,
	String name,
	int rarity,
	String imageUrl,
	String imageBigUrl
) {
	public static CharacterResponse from(Character character) {
		return new CharacterResponse(
			character.getId(),
			character.getName(),
			character.getRarity(),
			character.getImageUrl(),
			character.getImageBigUrl()
		);
	}

	public static CharacterResponse of(Character character, S3Bucket s3Bucket) {
		String rarityString = String.valueOf(character.getRarity());
		return new CharacterResponse(
			character.getId(),
			character.getName(),
			character.getRarity(),
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
