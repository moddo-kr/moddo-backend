package com.dnd.moddo.outbox.application.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OutboxEventScheduler {
	private final OutboxEventPublisher outboxEventPublisher;

	@Scheduled(fixedDelay = 5000)
	public void publishPendingEvents() {
		outboxEventPublisher.publishPendingEvents();
	}
}
