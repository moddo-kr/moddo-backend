package com.dnd.moddo.domain.group.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record GroupRequest(
        @NotBlank(message = "모임 이름은 필수입니다.")
        String name,

        String password,

        LocalDateTime expiredAt
) {
}
