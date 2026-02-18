package com.dnd.moddo.global.support;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.settlement.Settlement;

public class GroupTestFactory {
	public static Settlement createDefault() {
		return new Settlement(
			"group 1",
			1L,
			LocalDateTime.now().plusMinutes(1),
			"은행",
			"계좌",
			"code",
			LocalDateTime.now().plusDays(1)
		);
	}

	public static Settlement createWithCode(String code) {
		return new Settlement(
			"group 1",
			1L,
			LocalDateTime.now().plusMinutes(1),
			"은행",
			"계좌",
			code,
			LocalDateTime.now().plusDays(1)
		);
	}
}
