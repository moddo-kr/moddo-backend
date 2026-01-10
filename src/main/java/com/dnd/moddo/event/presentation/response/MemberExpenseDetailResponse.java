package com.dnd.moddo.event.presentation.response;

import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;

public record MemberExpenseDetailResponse(
	String content,
	Long amount
) {
	public static MemberExpenseDetailResponse of(Expense expense, MemberExpense memberExpense) {
		return new MemberExpenseDetailResponse(expense.getContent(), memberExpense.getAmount());
	}
}
