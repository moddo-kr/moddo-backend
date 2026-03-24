package com.dnd.moddo.outbox.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.task.EventTask;
import com.dnd.moddo.outbox.domain.task.type.EventTaskType;
import com.dnd.moddo.outbox.infrastructure.EventTaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventTaskCreator {
	private final EventTaskRepository eventTaskRepository;

	public EventTask create(OutboxEvent outboxEvent, EventTaskType taskType, Long targetUserId) {
		return eventTaskRepository.save(EventTask.pending(outboxEvent, taskType, targetUserId));
	}

	public List<EventTask> createAll(List<EventTask> eventTasks) {
		return eventTaskRepository.saveAll(eventTasks);
	}
}
