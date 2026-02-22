package com.dnd.moddo.auth.presentation.response;

public record AuthCheckResponse(
	boolean authenticated,
	UserInfo user,
	AuthFailReason reason
) {
	public static AuthCheckResponse success(Long id, String role) {
		return new AuthCheckResponse(true, new UserInfo(id, role), null);
	}

	public static AuthCheckResponse fail(AuthFailReason reason) {
		return new AuthCheckResponse(false, null, reason);
	}

	public enum AuthFailReason {
		NO_TOKEN,
		TOKEN_EXPIRED,
		INVALID_TOKEN
	}

	public record UserInfo(
		Long id,
		String role
	) {
	}

}

