package com.dnd.moddo.event.presentation.response;

import java.util.List;

import com.dnd.moddo.event.domain.memberExpense.MemberExpense;

public record MemberExpensesResponse(
	List<MemberExpenseResponse> memberExpenses
) {
	public static MemberExpensesResponse of(List<MemberExpense> memberExpenses) {
		return new MemberExpensesResponse(
			memberExpenses.stream()
				.map(MemberExpenseResponse::of)
				.toList()
		);
	}
}
