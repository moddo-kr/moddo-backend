package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.request.ExpenseRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpenseCreator {
	private final ExpenseRepository expenseRepository;
	private final MemberExpenseValidator memberExpenseValidator;
	private final SettlementRepository settlementRepository;

	public Expense create(Long settlementId, ExpenseRequest request) {
		Settlement settlement = settlementRepository.getById(settlementId);

		memberExpenseValidator.validateMembersArePartOfSettlement(settlementId, request.memberExpenses());

		Expense expense = request.toEntity(settlement);
		return expenseRepository.save(expense);
	}

}
