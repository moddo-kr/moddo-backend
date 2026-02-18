package com.dnd.moddo.user.presentation.request;

import java.time.LocalDateTime;

import com.dnd.moddo.user.domain.Authority;
import com.dnd.moddo.user.domain.User;

public record GuestUserSaveRequest(String email, String name) {
	public User toEntity() {
		return User.builder()
			.email(email)
			.name(name)
			.kakaoId(null)
			.isMember(false)
			.authority(Authority.USER)
			.profile(null)
			.createdAt(LocalDateTime.now())
			.expiredAt(LocalDateTime.now().plusMonths(1))
			.build();
	}
}
