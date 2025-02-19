package com.dnd.moddo.domain.expense.dto.request;

import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.group.entity.Group;

import jakarta.validation.Valid;

public record ExpensesRequest(
	@Valid List<ExpenseRequest> expenses
) {
	public List<Expense> toEntity(Group group) {
		return expenses.stream()
			.map(e -> e.toEntity(group))
			.toList();
	}
}
