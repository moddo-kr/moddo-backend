package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class MemberAlreadyAssignedException extends ModdoException {
	public MemberAlreadyAssignedException() {
		super(HttpStatus.BAD_REQUEST, "이미 다른 사용자가 선택한 참여자입니다.");
	}
}
