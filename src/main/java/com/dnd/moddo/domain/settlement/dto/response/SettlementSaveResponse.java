package com.dnd.moddo.domain.settlement.dto.response;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;

public record SettlementSaveResponse(
	String groupToken,
	AppointmentMemberResponse manager
) {
}
