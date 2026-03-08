package com.dnd.moddo.user.presentation.response;

import com.dnd.moddo.user.domain.User;

import lombok.Builder;

@Builder
public record UserResponse(String name, String email, String profile) {

	public static UserResponse of(User user) {
		return UserResponse.builder()
			.name(user.getName())
			.email(user.getEmail())
			.profile(user.getProfile())
			.build();

	}
}
