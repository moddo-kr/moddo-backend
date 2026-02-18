package com.dnd.moddo.auth.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponse(
	@JsonProperty("access_token") String accessToken,
	@JsonProperty("expires_in") int expiresIn
) {
}