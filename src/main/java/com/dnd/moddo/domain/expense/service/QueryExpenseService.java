package com.dnd.moddo.domain.expense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.memberExpense.service.QueryMemberExpenseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryExpenseService {
	private final ExpenseReader expenseReader;
	private final QueryMemberExpenseService queryMemberExpenseService;

	public ExpensesResponse findAllByGroupId(Long groupId) {
		List<Expense> expenses = expenseReader.findAllByGroupId(groupId);
		return new ExpensesResponse(
			expenses.stream()
				.map(expense ->
					ExpenseResponse.of(expense, queryMemberExpenseService.findAllByExpenseId(expense.getId()))
				).toList()
		);
	}

	public ExpenseResponse findOneByExpenseId(Long expenseId) {
		Expense expense = expenseReader.findOneByExpenseId(expenseId);
		return ExpenseResponse.of(expense);
	}
}
