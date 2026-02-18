package com.dnd.moddo.auth.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoProfile(
	Long id,
	@JsonProperty("kakao_account") KakaoAccount kakaoAccount,
	Properties properties
) {
	public record Properties(
		String nickname
	) {
	}

	public record KakaoAccount(
		String email,
		Profile profile
	) {
	}

	public record Profile(
		String nickname
	) {
	}
}