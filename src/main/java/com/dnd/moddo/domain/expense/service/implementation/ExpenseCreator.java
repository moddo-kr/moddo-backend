package com.dnd.moddo.domain.expense.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseValidator;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.repository.SettlementRepository;

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
