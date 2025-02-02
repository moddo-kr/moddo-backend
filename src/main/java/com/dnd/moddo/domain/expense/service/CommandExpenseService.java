package com.dnd.moddo.domain.expense.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.request.ExpenseSaveRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseCreator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandExpenseService {
	private final ExpenseCreator expenseCreator;

	public ExpenseResponse createExpense(Long meetId, ExpenseSaveRequest request) {
		Expense expense = expenseCreator.create(meetId, request);
		return ExpenseResponse.of(expense);
	}
}
