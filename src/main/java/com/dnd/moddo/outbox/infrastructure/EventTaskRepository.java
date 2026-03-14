package com.dnd.moddo.outbox.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskStatus;

import jakarta.persistence.EntityNotFoundException;

public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
	List<EventTask> findTop30ByStatusInAndAttemptCountLessThanOrderByCreatedAtAsc(
		List<EventTaskStatus> statuses,
		int attemptCount
	);

	List<EventTask> findTop10ByStatusOrderByCreatedAtDesc(EventTaskStatus status);

	default EventTask getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Event task not found: " + id));
	}
}
