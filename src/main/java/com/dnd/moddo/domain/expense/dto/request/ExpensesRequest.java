package com.dnd.moddo.domain.expense.dto.request;

import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.group.entity.Group;

public record ExpensesRequest(
	List<ExpenseRequest> expenses
) {
	public List<Expense> toEntity(Group group, int maxOrder) {
		return expenses.stream()
			.map(e -> e.toEntity(group, maxOrder))
			.toList();
	}
}
