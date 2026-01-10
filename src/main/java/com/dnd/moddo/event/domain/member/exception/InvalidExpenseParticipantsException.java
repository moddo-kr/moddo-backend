package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class InvalidExpenseParticipantsException extends ModdoException {
	public InvalidExpenseParticipantsException() {
		super(HttpStatus.BAD_REQUEST, "총무(MANAGER)는 한 명 있어야 합니다.");
	}
}
