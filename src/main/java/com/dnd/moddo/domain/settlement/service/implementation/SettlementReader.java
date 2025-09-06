package com.dnd.moddo.domain.settlement.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.settlement.dto.response.SettlementHeaderResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementReader {
	private final SettlementRepository settlementRepository;
	private final AppointmentMemberRepository appointmentMemberRepository;
	private final ExpenseRepository expenseRepository;

	public Settlement read(Long settlementId) {
		return settlementRepository.getById(settlementId);
	}

	public List<AppointmentMember> findBySettlement(Long settlementId) {
		return appointmentMemberRepository.findBySettlementId(settlementId);
	}

	public SettlementHeaderResponse findByHeader(Long settlementId) {
		Settlement settlement = settlementRepository.getById(settlementId);
		Long totalAmount = expenseRepository.sumAmountBySettlement(settlement);

		return SettlementHeaderResponse.of(settlement.getName(), totalAmount, settlement.getDeadline(),
			settlement.getBank(),
			settlement.getAccountNumber());
	}

	public Long findIdByGroupCode(String code) {
		return settlementRepository.getIdByCode(code);
	}
}
