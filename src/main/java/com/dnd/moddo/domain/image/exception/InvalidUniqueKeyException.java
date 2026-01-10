package com.dnd.moddo.domain.image.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class InvalidUniqueKeyException extends ModdoException {
	public InvalidUniqueKeyException() {
		super(HttpStatus.UNAUTHORIZED, "유효하지 않는 uniqueKey입니다.");
	}
}
