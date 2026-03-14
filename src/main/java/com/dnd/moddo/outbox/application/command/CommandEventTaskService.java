package com.dnd.moddo.outbox.application.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.application.impl.EventTaskProcessor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandEventTaskService {
	private final EventTaskProcessor eventTaskProcessor;

	public void retry(Long eventTaskId) {
		eventTaskProcessor.process(eventTaskId);
	}
}
