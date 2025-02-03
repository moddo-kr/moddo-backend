package com.dnd.moddo.global.jwt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        ZonedDateTime expiredAt,
        Boolean isMember
) {
}
