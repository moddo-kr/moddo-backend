package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class ExpenseRoleNotFoundException extends ModdoException {
	public ExpenseRoleNotFoundException(String role) {
		super(HttpStatus.NOT_FOUND, "해당 역할은 존재하지 않습니다. (Role: " + role + ")");
	}
}
