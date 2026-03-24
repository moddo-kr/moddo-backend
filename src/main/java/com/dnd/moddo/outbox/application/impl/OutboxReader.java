package com.dnd.moddo.outbox.application.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.domain.event.OutboxEvent;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventStatus;
import com.dnd.moddo.outbox.infrastructure.OutboxEventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboxReader {
	private final OutboxEventRepository outboxEventRepository;

	public Slice<OutboxEvent> findByStatus(OutboxEventStatus status, Pageable pageable) {
		return outboxEventRepository.findByStatusOrderByIdAsc(status, pageable);
	}

	public OutboxEvent findById(Long outboxEventId) {
		return outboxEventRepository.getById(outboxEventId);
	}
}
