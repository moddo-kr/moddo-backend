package com.dnd.moddo.domain.memberExpense.dto.response;

import java.util.List;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

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
