package com.dnd.moddo.domain.appointmentMember.dto.response;

import java.util.List;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;

public record AppointmentMembersResponse(List<AppointmentMemberResponse> members) {
	public static AppointmentMembersResponse of(List<AppointmentMember> members) {
		return new AppointmentMembersResponse(members.stream()
			.map(AppointmentMemberResponse::of)
			.toList());
	}
}
