package com.dnd.moddo.reward.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.image.domain.exception.CharacterNotFoundException;
import com.dnd.moddo.reward.domain.character.Character;

public interface CharacterRepository extends JpaRepository<Character, Long> {
	List<Character> findByRarity(int rarity);

	default Character getById(Long characterId) {
		return findById(characterId).orElseThrow(() -> new CharacterNotFoundException());
	}
}