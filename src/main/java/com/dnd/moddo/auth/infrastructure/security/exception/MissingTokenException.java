package com.dnd.moddo.auth.infrastructure.security.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class MissingTokenException extends ModdoException {
	public MissingTokenException() {
		super(HttpStatus.UNAUTHORIZED, "토큰이 없습니다.");
	}
}
