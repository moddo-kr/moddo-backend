package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class InvalidMemberException extends ModdoException {
	public InvalidMemberException(Long id) {
		super(HttpStatus.BAD_REQUEST, "해당 모임에 속하지 않은 참여자가 포함되어 있습니다 (Member ID: " + id + ")");
	}
}
