package com.dnd.moddo.domain.expense.dto.request;

import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.settlement.entity.Settlement;

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
