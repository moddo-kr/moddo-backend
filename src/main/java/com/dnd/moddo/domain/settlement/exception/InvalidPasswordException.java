package com.dnd.moddo.domain.settlement.exception;

import com.dnd.moddo.global.exception.ModdoException;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ModdoException {
	public InvalidPasswordException() {
		super(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다.");
	}
}
