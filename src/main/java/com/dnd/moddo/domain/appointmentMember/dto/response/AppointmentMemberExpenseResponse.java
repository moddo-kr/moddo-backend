package com.dnd.moddo.domain.appointmentMember.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseDetailResponse;

import lombok.Builder;

@Builder
public record AppointmentMemberExpenseResponse(
	Long id,
	ExpenseRole role,
	String name,
	Long totalAmount,
	String profile,
	boolean isPaid,
	LocalDateTime paidAt,
	List<MemberExpenseDetailResponse> expenses
) {
	public static AppointmentMemberExpenseResponse of(AppointmentMember appointmentMember, Long totalAmount,
		List<MemberExpenseDetailResponse> expenses) {
		return AppointmentMemberExpenseResponse.builder()
			.id(appointmentMember.getId())
			.role(appointmentMember.getRole())
			.name(appointmentMember.getName())
			.totalAmount(totalAmount)
			.profile(appointmentMember.getProfileUrl())
			.isPaid(appointmentMember.isPaid())
			.paidAt(appointmentMember.getPaidAt())
			.expenses(expenses).build();
	}
}
