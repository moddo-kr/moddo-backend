package com.dnd.moddo.outbox.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;

import jakarta.persistence.EntityNotFoundException;

public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
	List<EventTask> findTop30ByStatusInAndAttemptCountLessThanOrderByCreatedAtAsc(
		List<EventTaskStatus> statuses,
		int attemptCount
	);

	List<EventTask> findTop10ByStatusOrderByCreatedAtDesc(EventTaskStatus status);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		update EventTask eventTask
		set eventTask.status = :processingStatus
		where eventTask.id = :eventTaskId
			and eventTask.status in :claimableStatuses
			and eventTask.attemptCount < :maxRetryCount
		""")
	int claimProcessing(
		@Param("eventTaskId") Long eventTaskId,
		@Param("processingStatus") EventTaskStatus processingStatus,
		@Param("claimableStatuses") List<EventTaskStatus> claimableStatuses,
		@Param("maxRetryCount") int maxRetryCount
	);

	default EventTask getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Event task not found: " + id));
	}
}
