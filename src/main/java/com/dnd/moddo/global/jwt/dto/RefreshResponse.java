package com.dnd.moddo.global.jwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshResponse {
    private final String accessToken;

    public RefreshResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
