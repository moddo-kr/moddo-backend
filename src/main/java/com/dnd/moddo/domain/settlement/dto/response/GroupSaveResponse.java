package com.dnd.moddo.domain.settlement.dto.response;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;

public record GroupSaveResponse(
	String groupToken,
	AppointmentMemberResponse manager
) {
}
