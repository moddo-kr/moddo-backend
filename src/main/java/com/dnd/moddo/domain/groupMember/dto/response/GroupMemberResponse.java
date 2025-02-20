package com.dnd.moddo.domain.groupMember.dto.response;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;

import lombok.Builder;

@Builder
public record GroupMemberResponse(
	Long id,
	ExpenseRole role,
	String name,
	String profile,
	boolean isPaid,
	LocalDateTime paidAt
) {

	public static GroupMemberResponse of(GroupMember groupMember) {
		return GroupMemberResponse.builder()
			.id(groupMember.getId())
			.name(groupMember.getName())
			.role(groupMember.getRole())
			.isPaid(groupMember.isPaid())
			.paidAt(groupMember.getPaidAt())
			.profile(groupMember.getProfileUrl(groupMember.getId()))
			.build();
	}

}
