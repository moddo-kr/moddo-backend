package com.dnd.moddo.domain.groupMember.dto.response;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;

public record GroupMemberResponse(
	Long id,
	ExpenseRole role,
	String name,
	String profile,
	boolean isPaid
) {

	public static GroupMemberResponse of(GroupMember groupMember) {
		return new GroupMemberResponse(groupMember.getId(), groupMember.getRole(), groupMember.getName(),
			generateProfileUrl(groupMember.getProfileId()), groupMember.isPaid());
	}

	private static String generateProfileUrl(Integer profile) {
		if (profile == null) {
			return "https://example.com/profiles/default.jpg";
		}
		return "https://example.com/profiles/" + profile + ".jpg";
	}
}
