package com.dnd.moddo.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class TokenNotFoundException extends ModdoException {
	public TokenNotFoundException(String token) {
		super(HttpStatus.NOT_FOUND, token + "이 존재하지 않습니다.");
	}
}
