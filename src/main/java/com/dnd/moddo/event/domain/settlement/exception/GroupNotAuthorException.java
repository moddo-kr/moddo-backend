package com.dnd.moddo.event.domain.settlement.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class GroupNotAuthorException extends ModdoException {
	public GroupNotAuthorException() {
		super(HttpStatus.FORBIDDEN, "모임 작성자가 아닙니다.");
	}
}
