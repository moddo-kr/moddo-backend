package com.dnd.moddo.domain.expense.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.response.ExpenseDetailResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpenseDetailsResponse;
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
		Expense expense = expenseReader.findByExpenseId(expenseId);
		return ExpenseResponse.of(expense);
	}

	public ExpenseDetailsResponse findAllExpenseDetailsByGroupId(Long groupId) {

		List<Expense> expenses = expenseReader.findAllByGroupId(groupId);
		List<Long> expenseIds = expenses.stream().map(Expense::getId).toList();

		Map<Long, List<String>> memberNameByExpenseId = queryMemberExpenseService.getMemberNamesByExpenseIds(
			expenseIds);

		return new ExpenseDetailsResponse(
			expenses.stream()
				.map(expense ->
					ExpenseDetailResponse.of(expense, memberNameByExpenseId.get(expense.getId()))
				).toList()
		);
	}
}
