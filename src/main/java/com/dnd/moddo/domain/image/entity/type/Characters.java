package com.dnd.moddo.domain.image.entity.type;

import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Characters {
	LUCKY(1, "러키 모또", "lucky", "lucky"),
	ANGEL(2, "천사 모또", "angel", "angel"),
	STRAWBERRY(2, "딸기 또또", "strawberry", "strqwberry"),
	MAGIC(3, "마법사 또또", "magic", "magic"),
	SLEEP(3, "잠꾸러기 또또", "sleep", "sleep");

	private final int rarity;
	private final String name;
	private final String fileName;
	private final String bigName;

	public static List<Characters> getByRarity(int rarity) {
		return Arrays.stream(values())
			.filter(character -> character.getRarity() == rarity)
			.toList();
	}
}