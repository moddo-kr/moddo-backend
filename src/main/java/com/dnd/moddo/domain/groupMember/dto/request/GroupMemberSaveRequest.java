package com.dnd.moddo.domain.groupMember.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupMemberSaveRequest(
	@NotBlank(message = "참여자 이름으로 공백은 입력할 수 없습니다.")
	String name
) {

}
