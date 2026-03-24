package com.dnd.moddo.reward.domain.character;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "collections",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_collections_user_character", columnNames = {"user_id", "character_id"})
	}
)
public class Collection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime acquiredAt;

	@Column(name = "character_id", nullable = false)
	private Long characterId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Builder
	private Collection(LocalDateTime acquiredAt, Long characterId, Long userId) {
		this.acquiredAt = acquiredAt;
		this.characterId = characterId;
		this.userId = userId;
	}

	public static Collection acquire(Long userId, Long characterId) {
		return Collection.builder()
			.userId(userId)
			.characterId(characterId)
			.acquiredAt(LocalDateTime.now())
			.build();
	}

}
