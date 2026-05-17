package com.dnd.moddo.event.application.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.request.SettlementAccountRequest;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SettlementUpdater {
	private final SettlementRepository settlementRepository;

	public Settlement updateAccount(SettlementAccountRequest request, Long settlementId) {
		Settlement settlement = settlementRepository.getById(settlementId);
		settlement.updateAccount(request);
		return settlement;
	}

	public boolean complete(Long settlementId, LocalDateTime completedAt) {
		return settlementRepository.markCompletedIfNotCompleted(settlementId, completedAt) == 1;
	}
}
