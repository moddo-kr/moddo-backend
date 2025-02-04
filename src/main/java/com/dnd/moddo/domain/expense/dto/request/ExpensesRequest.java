package com.dnd.moddo.domain.expense.dto.request;

import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;

public record ExpensesRequest(
	List<ExpenseRequest> expenses
) {
	public List<Expense> toEntity(Long meetId) {
		return expenses.stream()
			.map(e -> e.toEntity(meetId))
			.toList();
	}
}
