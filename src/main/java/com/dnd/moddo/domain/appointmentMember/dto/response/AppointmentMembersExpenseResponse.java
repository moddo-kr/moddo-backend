package com.dnd.moddo.domain.appointmentMember.dto.response;

import java.util.List;

public record AppointmentMembersExpenseResponse(
	List<AppointmentMemberExpenseResponse> memberExpenses
) {

}
