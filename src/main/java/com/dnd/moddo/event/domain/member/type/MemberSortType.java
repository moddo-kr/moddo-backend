package com.dnd.moddo.event.domain.member.type;

import java.util.Arrays;

import com.dnd.moddo.event.domain.member.exception.MemberSortTypeNotFoundException;

public enum MemberSortType {
	CREATED,
	NAME,
	PAID_AT;

	public static MemberSortType from(String value) {
		return Arrays.stream(values())
			.filter(sortType -> sortType.name().equalsIgnoreCase(value))
			.findFirst()
			.orElseThrow(() -> new MemberSortTypeNotFoundException(value));
	}
}
