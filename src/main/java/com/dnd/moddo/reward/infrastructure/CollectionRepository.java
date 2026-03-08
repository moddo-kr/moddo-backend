package com.dnd.moddo.reward.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.reward.domain.character.Collection;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
}
