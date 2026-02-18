package com.dnd.moddo.event.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.event.infrastructure.SettlementQueryRepository;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementReader {
	private final SettlementRepository settlementRepository;
	private final MemberRepository memberRepository;
	private final ExpenseRepository expenseRepository;
	private final SettlementQueryRepository settlementQueryRepository;

	public Settlement read(Long settlementId) {
		return settlementRepository.getById(settlementId);
	}

	public List<Member> findBySettlement(Long settlementId) {
		return memberRepository.findBySettlementId(settlementId);
	}

	public SettlementHeaderResponse findByHeader(Long settlementId) {
		Settlement settlement = settlementRepository.getById(settlementId);
		Long totalAmount = expenseRepository.sumAmountBySettlement(settlement);

		return SettlementHeaderResponse.of(settlement.getName(), totalAmount, settlement.getDeadline(),
			settlement.getBank(),
			settlement.getAccountNumber());
	}

	@Transactional(readOnly = true)
	public Long findIdByGroupCode(String code) {
		return settlementRepository.getIdByCode(code);
	}

	public List<SettlementListResponse> findListByUserIdAndStatus(Long userId, SettlementStatus status) {
		return settlementQueryRepository.findByUserAndStatus(userId, status);

	}
}
