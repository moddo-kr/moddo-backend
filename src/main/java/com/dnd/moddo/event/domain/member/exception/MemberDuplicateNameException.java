package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class MemberDuplicateNameException extends ModdoException {
	public MemberDuplicateNameException() {
		super(HttpStatus.CONFLICT, "중복된 참여자의 이름은 저장할 수 없습니다.");
	}
}
