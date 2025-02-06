package com.dnd.moddo.domain.expense.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;

public record ExpenseResponse(
	Long id, Long amount, String content, LocalDate date,
	List<MemberExpenseResponse> memberExpenses
) {
	public static ExpenseResponse of(Expense expense, List<MemberExpenseResponse> memberExpenses) {
		return new ExpenseResponse(expense.getId(), expense.getAmount(), expense.getContent(), expense.getDate(),
			memberExpenses);
	}

	public static ExpenseResponse of(Expense expense) {
		return new ExpenseResponse(expense.getId(), expense.getAmount(), expense.getContent(), expense.getDate(), null);
	}
}
