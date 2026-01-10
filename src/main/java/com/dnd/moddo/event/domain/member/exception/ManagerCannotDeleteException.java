package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class ManagerCannotDeleteException extends ModdoException {
	public ManagerCannotDeleteException(Long appointmentMemberId) {
		super(HttpStatus.BAD_REQUEST, "총무(MANAGER)는 삭제할 수 없습니다. (Member ID: " + appointmentMemberId + ")");
	}
}
