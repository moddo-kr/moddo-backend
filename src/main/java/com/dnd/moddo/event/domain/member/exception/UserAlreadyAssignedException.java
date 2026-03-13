package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class UserAlreadyAssignedException extends ModdoException {
	public UserAlreadyAssignedException(Long userId) {
		super(HttpStatus.BAD_REQUEST, "이미 이 정산의 다른 참여자를 선택한 사용자입니다. (User ID: " + userId + ")");
	}
}
