package com.dnd.moddo.domain.user.dto.request;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.entity.type.Authority;

public record UserSaveRequest(
	String email,
	String name,
	Long kakaoId
) {
	public User toEntity() {
		return User.builder()
			.email(email)
			.name(name)
			.kakaoId(kakaoId)
			.isMember(true)
			.authority(Authority.USER)
			.profile(null)
			.createdAt(LocalDateTime.now())
			.expiredAt(LocalDateTime.now().plusMonths(1))
			.build();
	}
}
