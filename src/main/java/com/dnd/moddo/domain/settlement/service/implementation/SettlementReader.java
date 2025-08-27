package com.dnd.moddo.domain.settlement.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.repository.AppointmentMemberRepository;
import com.dnd.moddo.domain.settlement.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementReader {
	private final SettlementRepository settlementRepository;
	private final AppointmentMemberRepository appointmentMemberRepository;
	private final ExpenseRepository expenseRepository;

	public Settlement read(Long groupId) {
		return settlementRepository.getById(groupId);
	}

	public List<AppointmentMember> findByGroup(Long groupId) {
		return appointmentMemberRepository.findByGroupId(groupId);
	}

	public GroupHeaderResponse findByHeader(Long groupId) {
		Settlement settlement = settlementRepository.getById(groupId);
		Long totalAmount = expenseRepository.sumAmountByGroup(settlement);

		return GroupHeaderResponse.of(settlement.getName(), totalAmount, settlement.getDeadline(), settlement.getBank(),
			settlement.getAccountNumber());
	}

	public Long findIdByGroupCode(String code) {
		return settlementRepository.getIdByCode(code);
	}
}
