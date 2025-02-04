package com.dnd.moddo.domain.groupMember.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class GroupMemberNotFoundException extends ModdoException {
	public GroupMemberNotFoundException(Long groupMemberId) {
		super(HttpStatus.NOT_FOUND, "해당 지출내역을 찾을 수 없습니다. (GroupMember ID: " + groupMemberId + ")");
	}
}
