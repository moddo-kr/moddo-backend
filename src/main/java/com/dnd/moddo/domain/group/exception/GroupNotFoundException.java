package com.dnd.moddo.domain.group.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class GroupNotFoundException extends ModdoException {
	public GroupNotFoundException(Long groupId) {
		super(HttpStatus.NOT_FOUND, "아이디가 " + groupId + "인 모임을 찾을 수 없습니다.");
	}

	;

	public GroupNotFoundException(String code) {
		super(HttpStatus.NOT_FOUND, "그룹 코드가 " + code + "인 모임을 찾을 수 없습니다.");
	}

	;
}
