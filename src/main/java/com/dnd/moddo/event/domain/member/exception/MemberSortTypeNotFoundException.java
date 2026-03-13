package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class MemberSortTypeNotFoundException extends ModdoException {
	public MemberSortTypeNotFoundException(String sortType) {
		super(HttpStatus.BAD_REQUEST,
			"유효하지 않은 sortType입니다. 허용 값: CREATED, NAME, PAID_AT (입력값: " + sortType + ")");
	}
}
