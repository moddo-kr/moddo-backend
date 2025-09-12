package com.dnd.moddo.domain.settlement.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.settlement.entity.Settlement;

public record SettlementDetailResponse(
	Long id,
	String groupName,
	List<AppointmentMemberResponse> members
) {
	public static SettlementDetailResponse of(Settlement settlement, List<AppointmentMember> members) {
		List<AppointmentMemberResponse> memberResponses = members.stream()
			.map(AppointmentMemberResponse::of)
			.collect(Collectors.toList());
		return new SettlementDetailResponse(
			settlement.getId(),
			settlement.getName(),
			memberResponses
		);
	}
}
