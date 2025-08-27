package com.dnd.moddo.domain.appointmentMember.dto.response;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;

import lombok.Builder;

@Builder
public record AppointmentMemberResponse(
	Long id,
	ExpenseRole role,
	String name,
	String profile,
	boolean isPaid,
	LocalDateTime paidAt
) {

	public static AppointmentMemberResponse of(AppointmentMember appointmentMember) {
		return AppointmentMemberResponse.builder()
			.id(appointmentMember.getId())
			.name(appointmentMember.getName())
			.role(appointmentMember.getRole())
			.isPaid(appointmentMember.isPaid())
			.paidAt(appointmentMember.getPaidAt())
			.profile(appointmentMember.getProfileUrl())
			.build();
	}

}
