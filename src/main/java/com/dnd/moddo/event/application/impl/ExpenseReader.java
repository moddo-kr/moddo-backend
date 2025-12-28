package com.dnd.moddo.event.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ExpenseReader {
	private final ExpenseRepository expenseRepository;

	public List<Expense> findAllBySettlementId(Long settlementId) {
		return expenseRepository.findBySettlementIdOrderByDateAsc(settlementId);
	}

	public Expense findByExpenseId(Long expenseId) {
		return expenseRepository.getById(expenseId);
	}

}
