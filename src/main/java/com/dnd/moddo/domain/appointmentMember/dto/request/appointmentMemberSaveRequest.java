package com.dnd.moddo.domain.appointmentMember.dto.request;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.settlement.entity.Settlement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record appointmentMemberSaveRequest(
	@NotBlank(message = "이름은 필수 입력값 입니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z]{1,5}$", message = "참여자 이름은 한글과 영어만 포함하여 5자 이내여야 합니다.")
	String name
) {
	public AppointmentMember toEntity(Settlement settlement, Integer profileId, ExpenseRole role) {
		return AppointmentMember.builder()
			.name(name)
			.settlement(settlement)
			.role(role)
			.profileId(profileId)
			.isPaid(false)
			.build();
	}
}
