package com.dnd.moddo.domain.memberExpense.dto.response;

import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

import lombok.Builder;

@Builder
public record MemberExpenseResponse(
	Long id,
	ExpenseRole role,
	String name,
	String profile,
	Long amount
) {
	public static MemberExpenseResponse of(MemberExpense memberExpense) {
		return MemberExpenseResponse.builder()
			.id(memberExpense.getGroupMember().getId())
			.name(memberExpense.getGroupMember().getName())
			.role(memberExpense.getGroupMember().getRole())
			.profile(memberExpense.getGroupMember().getProfileUrl())
			.amount(memberExpense.getAmount())
			.build();
	}
}
