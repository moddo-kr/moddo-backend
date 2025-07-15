package com.dnd.moddo.domain.user.dto.request;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.entity.type.Authority;

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
