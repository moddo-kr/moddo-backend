package com.dnd.moddo.domain.groupMember.dto.request;

import java.util.List;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;

public record GroupMembersSaveRequest(
	List<GroupMemberSaveRequest> members
) {
	public List<GroupMember> toEntity(Long meetId) {
		return members.stream()
			.map(m -> new GroupMember(m.name(), meetId))
			.toList();
	}
}
