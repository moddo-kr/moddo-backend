package com.dnd.moddo.domain.groupMember.dto.request;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;

import jakarta.validation.constraints.NotBlank;

public record GroupMemberSaveRequest(
	@NotBlank(message = "참여자 이름으로 공백은 입력할 수 없습니다.")
	String name
) {
	public GroupMember toEntity(Group group) {
		return new GroupMember(name(), group);
	}
}
