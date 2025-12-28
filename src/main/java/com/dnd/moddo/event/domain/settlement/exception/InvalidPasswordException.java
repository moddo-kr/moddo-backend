package com.dnd.moddo.event.domain.settlement.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class InvalidPasswordException extends ModdoException {
	public InvalidPasswordException() {
		super(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다.");
	}
}
