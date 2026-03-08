package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberResponse(
	Long id,
	ExpenseRole role,
	String name,
	String profile,
	Long userId,
	Boolean isPaid,
	LocalDateTime paidAt
) {

	public static MemberResponse of(Member member) {
		return MemberResponse.builder()
			.id(member.getId())
			.name(member.getName())
			.role(member.getRole())
			.userId(member.getId())
			.isPaid(member.isPaid())
			.paidAt(member.getPaidAt())
			.profile(member.getProfileUrl())
			.build();
	}

	public MemberResponse(Long id, ExpenseRole role, String name, Long userId) {
		this(id, role, name, null, userId, null, null);
	}

}
