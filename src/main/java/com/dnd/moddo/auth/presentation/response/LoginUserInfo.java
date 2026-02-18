package com.dnd.moddo.auth.presentation.response;

public record LoginUserInfo(
	Long userId,
	String role
) {
}