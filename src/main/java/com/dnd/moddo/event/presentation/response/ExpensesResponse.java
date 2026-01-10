package com.dnd.moddo.event.presentation.response;

import java.util.List;

import com.dnd.moddo.event.domain.expense.Expense;

public record ExpensesResponse(
	List<ExpenseResponse> expenses
) {
	public static ExpensesResponse of(List<Expense> expenses) {
		return new ExpensesResponse(
			expenses.stream()
				.map(ExpenseResponse::of)
				.toList()
		);
	}
}
