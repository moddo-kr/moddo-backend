package com.dnd.moddo.event.presentation.request;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberSaveRequest(
	@NotBlank(message = "이름은 필수 입력값 입니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z]{1,5}$", message = "참여자 이름은 한글과 영어만 포함하여 5자 이내여야 합니다.")
	String name
) {
	public Member toEntity(Settlement settlement, Integer profileId, ExpenseRole role) {
		return Member.builder()
			.name(name)
			.settlement(settlement)
			.role(role)
			.profileId(profileId)
			.isPaid(false)
			.build();
	}
}
