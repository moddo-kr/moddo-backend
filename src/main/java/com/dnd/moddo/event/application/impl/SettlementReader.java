package com.dnd.moddo.event.application.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.member.type.MemberSortType;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.type.SettlementSortType;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.infrastructure.MemberQueryRepository;
import com.dnd.moddo.event.infrastructure.SettlementQueryRepository;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.dnd.moddo.event.presentation.response.SettlementShareResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementReader {
	private final SettlementRepository settlementRepository;
	private final ExpenseRepository expenseRepository;
	private final SettlementQueryRepository settlementQueryRepository;
	private final MemberQueryRepository memberQueryRepository;

	public Settlement read(Long settlementId) {
		return settlementRepository.getById(settlementId);
	}

	public List<Member> findBySettlement(Long settlementId) {
		return memberQueryRepository.findAllBySettlementId(settlementId, MemberSortType.CREATED);
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

	@Transactional(readOnly = true)
	public List<SettlementListResponse> findListByUserIdAndStatus(Long userId, SettlementStatus status,
		SettlementSortType sort, int limit) {
		return settlementQueryRepository.findByUserAndStatus(userId, status, sort, limit);

	}

	@Transactional(readOnly = true)
	public List<SettlementShareResponse> findSettlementListByUserId(Long userId) {

		List<SettlementShareResponse> settlements =
			settlementQueryRepository.findBySettlementList(userId);

		List<Long> ids = settlements.stream()
			.map(SettlementShareResponse::getSettlementId)
			.toList();

		Map<Long, List<MemberResponse>> memberMap =
			memberQueryRepository.findMembersByIds(ids);

		return settlements.stream()
			.map(s -> new SettlementShareResponse(
				s.getSettlementId(),
				s.getName(),
				s.getGroupCode(),
				s.getCreatedAt(),
				s.getCompletedAt(),
				memberMap.getOrDefault(s.getSettlementId(), List.of())
			))
			.toList();
	}
}
