package com.dnd.moddo.event.presentation.response;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;

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
			.id(memberExpense.getMember().getId())
			.name(memberExpense.getMember().getName())
			.role(memberExpense.getMember().getRole())
			.profile(memberExpense.getMember().getProfileUrl())
			.amount(memberExpense.getAmount())
			.build();
	}
}
