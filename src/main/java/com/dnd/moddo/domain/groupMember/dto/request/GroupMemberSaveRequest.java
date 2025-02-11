package com.dnd.moddo.domain.groupMember.dto.request;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;

import jakarta.validation.constraints.NotBlank;

public record GroupMemberSaveRequest(
	@NotBlank(message = "참여자 이름으로 공백은 입력할 수 없습니다.")
	String name,
	String role
) {
	public GroupMember toEntity(Group group, ExpenseRole role) {
		return new GroupMember(name(), group, role);
	}
}
