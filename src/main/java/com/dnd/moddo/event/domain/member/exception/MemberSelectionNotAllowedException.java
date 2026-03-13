package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class MemberSelectionNotAllowedException extends ModdoException {
	public MemberSelectionNotAllowedException(Long memberId) {
		super(HttpStatus.BAD_REQUEST, "선택하거나 해제할 수 없는 참여자입니다. (Member ID: " + memberId + ")");
	}
}
