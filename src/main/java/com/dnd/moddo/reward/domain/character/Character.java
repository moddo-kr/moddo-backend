package com.dnd.moddo.reward.domain.character;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "characters")
public class Character {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private int rarity;
	private String imageUrl;
	private String imageBigUrl;

	@Builder
	public Character(String name, int rarity, String imageUrl, String imageBigUrl) {
		this.name = name;
		this.rarity = rarity;
		this.imageUrl = imageUrl;
		this.imageBigUrl = imageBigUrl;
	}
}
