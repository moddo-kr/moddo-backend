package com.dnd.moddo.character.entity;

import com.dnd.moddo.domain.group.entity.Group;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	private String name;
	private String rarity;
	private String imageUrl;
	private String imageBigUrl;

	@Builder
	public Character(Group group, String name, String rarity, String imageUrl, String imageBigUrl) {
		this.group = group;
		this.name = name;
		this.rarity = rarity;
		this.imageUrl = imageUrl;
		this.imageBigUrl = imageBigUrl;
	}
}
