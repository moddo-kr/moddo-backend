package com.dnd.moddo.support;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.group.entity.Group;

public class GroupTestFactory {
	public static Group createDefault() {
		return new Group(
			"group 1",
			1L,
			"1234",
			LocalDateTime.now().plusMinutes(1),
			"은행",
			"계좌",
			"code",
			LocalDateTime.now().plusDays(1)
		);
	}

	public static Group createWithCode(String code) {
		return new Group(
			"group 1",
			1L,
			"1234",
			LocalDateTime.now().plusMinutes(1),
			"은행",
			"계좌",
			code,
			LocalDateTime.now().plusDays(1)
		);
	}
}
