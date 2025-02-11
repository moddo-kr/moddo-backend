package com.dnd.moddo.domain.memberExpense.dto.response;

import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public record MemberExpenseResponse(
	Long id,
	ExpenseRole role,
	String name,
	Long amount
) {
	public static MemberExpenseResponse of(MemberExpense memberExpense) {
		return new MemberExpenseResponse(
			memberExpense.getGroupMember().getId(),
			memberExpense.getGroupMember().getRole(),
			memberExpense.getGroupMember().getName(),
			memberExpense.getAmount());
	}
}
