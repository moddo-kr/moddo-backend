package com.dnd.moddo.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class UserPermissionException extends ModdoException {
	public UserPermissionException() {
		super(HttpStatus.UNAUTHORIZED, "해당 요청을 수행할 권한이 없습니다.");
	}
}
