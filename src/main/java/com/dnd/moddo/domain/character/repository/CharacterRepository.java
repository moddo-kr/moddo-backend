package com.dnd.moddo.domain.character.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.domain.character.entity.Character;
import com.dnd.moddo.domain.image.exception.CharacterNotFoundException;

public interface CharacterRepository extends JpaRepository<Character, Long> {
	default Character getById(Long characterId) {
		return findById(characterId).orElseThrow(() -> new CharacterNotFoundException());
	}

	Optional<Character> findByGroupId(Long groupId);
}