package com.dnd.moddo.reward.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.image.domain.exception.CharacterNotFoundException;
import com.dnd.moddo.reward.domain.character.Character;

public interface CharacterRepository extends JpaRepository<Character, Long> {
	default Character getById(Long characterId) {
		return findById(characterId).orElseThrow(() -> new CharacterNotFoundException());
	}

	Optional<Character> findBySettlementId(Long settlementId);
}