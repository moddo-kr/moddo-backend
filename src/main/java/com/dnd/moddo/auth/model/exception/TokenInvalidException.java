package com.dnd.moddo.auth.model.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class TokenInvalidException extends ModdoException {
	public TokenInvalidException() {
		super(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다.");
	}
}