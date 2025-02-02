package com.dnd.moddo.domain.expense.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseCreator;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseUpdater;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandExpenseService {
	private final ExpenseCreator expenseCreator;
	private final ExpenseUpdater expenseUpdater;

	public ExpenseResponse createExpense(Long meetId, ExpenseRequest request) {
		Expense expense = expenseCreator.create(meetId, request);
		return ExpenseResponse.of(expense);
	}

	public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request) {
		Expense expense = expenseUpdater.update(expenseId, request);
		return ExpenseResponse.of(expense);

	}
}
