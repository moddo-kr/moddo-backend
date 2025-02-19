package com.dnd.moddo.domain.groupMember.dto.request;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GroupMemberSaveRequest(
	@NotBlank(message = "이름은 필수 입력값 입니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z]{1,5}$", message = "참여자 이름은 한글과 영어만 포함하여 5자 이내여야 합니다.")
	String name

) {
	public GroupMember toEntity(Group group, ExpenseRole role) {
		return new GroupMember(name(), group, role);
	}
}
