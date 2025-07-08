package com.dnd.moddo.global.support;

import static com.dnd.moddo.domain.user.entity.type.Authority.*;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.user.entity.User;

public class UserTestFactory {
	public static User createGuestDefault() {
		LocalDateTime time = LocalDateTime.now();

		return new User(
			"연노른자",
			"guest-UUID1@guest.com",
			"profile.png",
			false,
			USER,
			time,
			time.plusDays(7));
	}

	public static User createWithEmail(String email) {
		LocalDateTime time = LocalDateTime.now();

		return new User(
			"연노른자",
			email,
			"profile.png",
			true,
			USER,
			time,
			time.plusDays(7));
	}
}
