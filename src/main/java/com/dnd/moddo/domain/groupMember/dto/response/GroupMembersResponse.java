package com.dnd.moddo.domain.groupMember.dto.response;

import java.util.List;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;

public record GroupMembersResponse(List<GroupMemberResponse> members) {
	public static GroupMembersResponse of(List<GroupMember> members) {
		return new GroupMembersResponse(members.stream()
			.map(GroupMemberResponse::of)
			.toList());
	}
}
