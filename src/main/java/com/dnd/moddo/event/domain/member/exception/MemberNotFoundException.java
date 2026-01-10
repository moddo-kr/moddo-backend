package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class MemberNotFoundException extends ModdoException {
	public MemberNotFoundException(Long appointmentMemberId) {
		super(HttpStatus.NOT_FOUND, "해당 참여자를 찾을 수 없습니다. (AppointmentMember ID: " + appointmentMemberId + ")");
	}
}
