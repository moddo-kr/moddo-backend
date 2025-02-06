package com.dnd.moddo.domain.memberExpense.dto.response;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public record MemberExpenseResponse(
	String name,
	Long amount
) {
	public static MemberExpenseResponse of(MemberExpense memberExpense) {
		return new MemberExpenseResponse(memberExpense.getGroupMember().getName(), memberExpense.getAmount());
	}
}
