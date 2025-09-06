package com.dnd.moddo.domain.settlement.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.settlement.dto.request.SettlementAccountRequest;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;

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
}
