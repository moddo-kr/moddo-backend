package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class MemberSelectionUnauthorizedException extends ModdoException {
	public MemberSelectionUnauthorizedException(Long memberId) {
		super(HttpStatus.FORBIDDEN, "본인이 선택한 참여자만 해제할 수 있습니다. (Member ID: " + memberId + ")");
	}
}
