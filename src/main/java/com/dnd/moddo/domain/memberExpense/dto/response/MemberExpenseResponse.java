package com.dnd.moddo.domain.memberExpense.dto.response;

import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
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
			.id(memberExpense.getAppointmentMember().getId())
			.name(memberExpense.getAppointmentMember().getName())
			.role(memberExpense.getAppointmentMember().getRole())
			.profile(memberExpense.getAppointmentMember().getProfileUrl())
			.amount(memberExpense.getAmount())
			.build();
	}
}
