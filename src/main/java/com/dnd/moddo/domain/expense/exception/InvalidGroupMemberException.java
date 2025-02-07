package com.dnd.moddo.domain.expense.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class InvalidGroupMemberException extends ModdoException {
	public InvalidGroupMemberException(Long id) {
		super(HttpStatus.BAD_REQUEST, "해당 모임에 속하지 않은 참여자가 포함되어 있습니다 (Member ID: " + id + ")");
	}
}
