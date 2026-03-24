package com.dnd.moddo.outbox.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;

import jakarta.persistence.EntityNotFoundException;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
	Slice<OutboxEvent> findByStatusOrderByIdAsc(OutboxEventStatus status, Pageable pageable);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		update OutboxEvent outboxEvent
		set outboxEvent.status = :processingStatus
		where outboxEvent.id = :outboxEventId
			and outboxEvent.status = :pendingStatus
		""")
	int claimProcessing(
		@Param("outboxEventId") Long outboxEventId,
		@Param("processingStatus") OutboxEventStatus processingStatus,
		@Param("pendingStatus") OutboxEventStatus pendingStatus
	);

	default OutboxEvent getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Outbox event not found: " + id));
	}
}
