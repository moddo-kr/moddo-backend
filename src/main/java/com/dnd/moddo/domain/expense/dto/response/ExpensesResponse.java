package com.dnd.moddo.domain.expense.dto.response;

import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;

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
