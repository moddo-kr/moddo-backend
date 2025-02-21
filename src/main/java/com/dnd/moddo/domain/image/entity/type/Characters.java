package com.dnd.moddo.domain.image.entity.type;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Characters {
	LUCKY(1, "러키 모또"),
	ANGEL(2, "천사 모또"),
	STRAWBERRY(2, "딸기 또또"),
	MAGIC(3, "마법사 또또"),
	SLEEP(3, "잠꾸러기 또또");

	private final int rarity;
	private final String name;

	public static List<Characters> getByRarity(int rarity) {
		return Arrays.stream(values())
			.filter(character -> character.getRarity() == rarity)
			.toList();
	}
}