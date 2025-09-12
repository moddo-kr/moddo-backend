package com.dnd.moddo.domain.settlement.exception;

import com.dnd.moddo.global.exception.ModdoException;

import org.springframework.http.HttpStatus;

public class GroupNotAuthorException extends ModdoException {
	public GroupNotAuthorException() {
		super(HttpStatus.FORBIDDEN, "모임 작성자가 아닙니다.");
	}
}
