package com.dnd.moddo.global.support;

import static com.dnd.moddo.domain.user.entity.type.Authority.*;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.user.entity.User;

public class UserTestFactory {
	/**
	 * Creates a default guest User instance with preset values for testing.
	 *
	 * The returned User has the name "연노른자", a guest email, profile image "profile.png",
	 * unverified status, USER authority, and timestamps set to the current time and seven days later.
	 *
	 * @return a User object representing a default guest user for test scenarios
	 */
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

	/**
	 * Creates a test User instance with the specified email and preset default values.
	 *
	 * The returned User has the name "연노른자", the provided email, profile image "profile.png",
	 * verified status set to true, authority set to USER, and creation and expiration timestamps
	 * based on the current time (expiration is 7 days after creation).
	 *
	 * @param email the email address to assign to the User
	 * @return a User instance configured for testing with the specified email
	 */
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
