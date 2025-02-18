package com.dnd.moddo.domain.image.dto;

public record CharacterResponse(
        String name,
        String rarity,
        String imageUrl,
        String imageBigUrl
) {
    public static CharacterResponse of(String name, String rarity, String imageUrl, String imageBigUrl) {
        return new CharacterResponse(name, rarity, imageUrl, imageBigUrl);
    }
}
