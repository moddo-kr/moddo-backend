package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.outbox.application.command.CommandOutboxEventService;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementCompletionProcessor {
	private final MemberReader memberReader;
	private final SettlementUpdater settlementUpdater;
	private final CommandOutboxEventService commandOutboxEventService;

	@Transactional
	public boolean completeIfAllPaid(Long settlementId) {
		if (memberReader.existsUnpaidMember(settlementId)) {
			return false;
		}

		boolean completed = settlementUpdater.complete(settlementId);
		if (completed) {
			commandOutboxEventService.create(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT,
				settlementId);
		}
		return completed;
	}
}
