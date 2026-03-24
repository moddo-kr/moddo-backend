package com.dnd.moddo.outbox.application.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventScheduler {
	private final OutboxEventPublisher outboxEventPublisher;

	@Scheduled(fixedDelay = 5000)
	public void publishPendingEvents() {
		try {
			outboxEventPublisher.publishPendingEvents();
		} catch (Exception e) {
			log.error("Error publishing events to outbox events queue", e);
		}
	}
}
