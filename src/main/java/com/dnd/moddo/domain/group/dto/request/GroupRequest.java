package com.dnd.moddo.domain.group.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupRequest(
	@NotBlank(message = "모임 이름은 필수입니다.")
	String name,

	String password
) {
}
