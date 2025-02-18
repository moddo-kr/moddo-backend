package com.dnd.moddo.domain.group.dto.response;

public record GroupPasswordResponse(
        String status
) {
    public static GroupPasswordResponse from(String status) {
        return new GroupPasswordResponse(status);
    }
}
