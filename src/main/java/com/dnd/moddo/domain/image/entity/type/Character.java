package com.dnd.moddo.domain.image.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Character {
    LUCKY(1, "lucky"),
    ANGEL(2, "angel"),
    STRAWBERRY(2, "strawberry"),
    MAGIC(3, "magic"),
    SLEEP(3, "sleep");

    private final int rarity;
    private final String name;

    public static List<Character> getByRarity(int rarity) {
        return Arrays.stream(values())
                .filter(character -> character.getRarity() == rarity)
                .toList();
    }
}