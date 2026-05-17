package com.dnd.moddo.event.application.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.outbox.application.command.CommandOutboxEventService;
import com.dnd.moddo.outbox.domain.event.type.AggregateType;
import com.dnd.moddo.outbox.domain.event.type.OutboxEventType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementCompletionProcessor {
	private final MemberReader memberReader;
	private final SettlementReader settlementReader;
	private final SettlementUpdater settlementUpdater;
	private final CommandOutboxEventService commandOutboxEventService;

	@Transactional
	public boolean completeIfAllPaid(Long settlementId) {
		if (memberReader.existsUnpaidMember(settlementId)) {
			return false;
		}

		return complete(settlementId);
	}

	@Transactional
	public boolean complete(Long settlementId) {
		Settlement settlement = settlementReader.read(settlementId);
		LocalDateTime completedAt = LocalDateTime.now();
		boolean completed = settlementUpdater.complete(settlementId, completedAt);
		if (completed && settlement.isCompletedWithinDeadline(completedAt)) {
			commandOutboxEventService.create(OutboxEventType.SETTLEMENT_COMPLETED, AggregateType.SETTLEMENT,
				settlementId);
		}
		return completed;
	}
}
