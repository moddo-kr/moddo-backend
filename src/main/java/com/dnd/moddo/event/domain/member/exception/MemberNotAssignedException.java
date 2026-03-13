package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class MemberNotAssignedException extends ModdoException {
	public MemberNotAssignedException(Long memberId) {
		super(HttpStatus.BAD_REQUEST, "아직 사용자가 선택하지 않은 참여자입니다. (Member ID: " + memberId + ")");
	}
}
