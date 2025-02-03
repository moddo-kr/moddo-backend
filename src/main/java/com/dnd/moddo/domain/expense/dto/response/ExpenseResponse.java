package com.dnd.moddo.domain.expense.dto.response;

import java.time.LocalDate;

import com.dnd.moddo.domain.expense.entity.Expense;

public record ExpenseResponse(
	Long id, Double amount, String content, LocalDate date
) {
	public static ExpenseResponse of(Expense expense) {
		return new ExpenseResponse(expense.getId(), expense.getAmount(), expense.getContent(), expense.getDate());
	}
}
