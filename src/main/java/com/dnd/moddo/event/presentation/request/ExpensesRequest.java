package com.dnd.moddo.event.presentation.request;

import java.util.List;

import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.settlement.Settlement;

import jakarta.validation.Valid;

public record ExpensesRequest(
	@Valid List<ExpenseRequest> expenses
) {
	public List<Expense> toEntity(Settlement settlement) {
		return expenses.stream()
			.map(e -> e.toEntity(settlement))
			.toList();
	}
}
