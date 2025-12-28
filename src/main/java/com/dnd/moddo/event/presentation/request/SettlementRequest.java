package com.dnd.moddo.event.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record SettlementRequest(
	@NotBlank(message = "모임 이름은 필수입니다.")
	String name,

	String password
) {
}
