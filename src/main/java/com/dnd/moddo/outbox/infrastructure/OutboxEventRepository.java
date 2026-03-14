package com.dnd.moddo.outbox.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;

import jakarta.persistence.EntityNotFoundException;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
	List<OutboxEvent> findAllByStatus(OutboxEventStatus status);

	default OutboxEvent getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Outbox event not found: " + id));
	}
}
