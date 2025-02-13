package com.dnd.moddo.domain.groupMember.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class ManagerCannotDeleteException extends ModdoException {
	public ManagerCannotDeleteException(Long groupMemberId) {
		super(HttpStatus.BAD_REQUEST, "총무(MANAGER)는 삭제할 수 없습니다. (Member ID: " + groupMemberId + ")");
	}
}
