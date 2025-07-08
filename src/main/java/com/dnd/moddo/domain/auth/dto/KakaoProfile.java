package com.dnd.moddo.domain.auth.dto;

public record KakaoProfile(
	Long id,
	String connected_at,
	Properties properties,
	KakaoAccount kakao_account
) {
	public record Properties(
		String nickname,
		String profile_image,
		String thumbnail_image
	) {
	}

	public record KakaoAccount(
		Boolean profile_nickname_needs_agreement,
		Boolean profile_image_needs_agreement,
		Profile profile,
		Boolean has_email,
		Boolean email_needs_agreement,
		Boolean is_email_valid,
		Boolean is_email_verified,
		String email
	) {
	}

	public record Profile(
		String nickname,
		String thumbnail_image_url,
		String profile_image_url,
		Boolean is_default_image,
		Boolean is_default_nickname
	) {
	}
}
