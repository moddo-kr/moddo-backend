package com.dnd.moddo.domain.groupMember.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class GroupMemberDuplicateNameException extends ModdoException {
	public GroupMemberDuplicateNameException() {
		super(HttpStatus.CONFLICT, "중복된 참여자의 이름은 저장할 수 없습니다.");
	}
}
