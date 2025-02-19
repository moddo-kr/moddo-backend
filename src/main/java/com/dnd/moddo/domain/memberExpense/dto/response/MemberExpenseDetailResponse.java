package com.dnd.moddo.domain.memberExpense.dto.response;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public record MemberExpenseDetailResponse(
	String content,
	Long amount
) {
	public static MemberExpenseDetailResponse of(Expense expense, MemberExpense memberExpense) {
		return new MemberExpenseDetailResponse(expense.getContent(), memberExpense.getAmount());
	}
}
