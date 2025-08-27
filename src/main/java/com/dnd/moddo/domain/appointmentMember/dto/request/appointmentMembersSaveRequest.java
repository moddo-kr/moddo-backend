package com.dnd.moddo.domain.appointmentMember.dto.request;

import static com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole.*;

import java.util.List;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.settlement.entity.Settlement;

import jakarta.validation.Valid;

public record appointmentMembersSaveRequest(
	@Valid List<appointmentMemberSaveRequest> members
) {
	public List<AppointmentMember> toEntity(Settlement settlement) {
		return members.stream()
			.map(m -> m.toEntity(settlement, null, PARTICIPANT))
			.toList();
	}

	public List<String> extractNames() {
		return members().stream().map(appointmentMemberSaveRequest::name).toList();
	}
}
