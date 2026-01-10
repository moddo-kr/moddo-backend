package com.dnd.moddo.event.presentation.response;

import java.time.LocalDate;
import java.util.List;

import com.dnd.moddo.event.domain.expense.Expense;

public record ExpenseResponse(
	Long id, Long amount, String content, LocalDate date,
	List<MemberExpenseResponse> memberExpenses
) {
	public static ExpenseResponse of(Expense expense, List<MemberExpenseResponse> memberExpenses) {
		return new ExpenseResponse(expense.getId(), expense.getAmount(), expense.getContent(),
			expense.getDate(),
			memberExpenses);
	}

	public static ExpenseResponse of(Expense expense) {
		return new ExpenseResponse(expense.getId(), expense.getAmount(), expense.getContent(),
			expense.getDate(), null);
	}
}
