package com.dnd.moddo.auth.presentation.response;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TokenResponse(
	String accessToken,
	String refreshToken,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	ZonedDateTime expiredAt,
	Boolean isMember
) {
}
