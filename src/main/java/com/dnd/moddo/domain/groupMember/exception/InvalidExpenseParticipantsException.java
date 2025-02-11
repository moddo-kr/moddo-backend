package com.dnd.moddo.domain.groupMember.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class InvalidExpenseParticipantsException extends ModdoException {
	public InvalidExpenseParticipantsException() {
		super(HttpStatus.BAD_REQUEST, "총무(MANAGER)는 한 명 있어야 합니다.");
	}
}
