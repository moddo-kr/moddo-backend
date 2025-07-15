package com.dnd.moddo.global.support;

import static com.dnd.moddo.domain.user.entity.type.Authority.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.dnd.moddo.domain.user.entity.User;

public class UserTestFactory {
	public static User createGuestDefault() {
		LocalDateTime time = LocalDateTime.now();

		return User
			.builder()
			.name("연노른자")
			.email("guest-" + UUID.randomUUID() + "@guest.com")
			.profile("profile.png")
			.isMember(false)
			.authority(USER)
			.createdAt(time)
			.expiredAt(time.plusDays(7))
			.build();
	}

	public static User createGuestWithNameAndEmail(String name, String email) {
		LocalDateTime time = LocalDateTime.now();

		return User
			.builder()
			.name(name)
			.email(email)
			.profile("profile.png")
			.isMember(false)
			.authority(USER)
			.createdAt(time)
			.expiredAt(time.plusDays(7))
			.build();
	}

	public static User createWithEmail(String email) {
		LocalDateTime time = LocalDateTime.now();
		return User
			.builder()
			.name("연노른자")
			.email(email)
			.profile("profile.png")
			.isMember(true)
			.kakaoId(1234565L)
			.authority(USER)
			.createdAt(time)
			.expiredAt(time.plusDays(7))
			.build();
	}
}
