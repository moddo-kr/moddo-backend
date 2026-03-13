package com.dnd.moddo.event.presentation.request;

import jakarta.validation.constraints.NotNull;

public record MemberSelectionRequest(
	@NotNull(message = "memberId는 필수입니다.")
	Long memberId
) {
}
