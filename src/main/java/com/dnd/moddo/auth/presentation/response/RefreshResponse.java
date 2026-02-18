package com.dnd.moddo.auth.presentation.response;

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
