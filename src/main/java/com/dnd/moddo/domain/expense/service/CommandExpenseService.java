package com.dnd.moddo.domain.expense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseCreator;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseUpdater;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandExpenseService {
	private final ExpenseCreator expenseCreator;
	private final ExpenseUpdater expenseUpdater;

	public ExpensesResponse createExpense(Long meetId, ExpensesRequest request) {
		List<Expense> expenses = expenseCreator.create(meetId, request);
		return ExpensesResponse.of(expenses);
	}

	public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request) {
		Expense expense = expenseUpdater.update(expenseId, request);
		return ExpenseResponse.of(expense);

	}
}
