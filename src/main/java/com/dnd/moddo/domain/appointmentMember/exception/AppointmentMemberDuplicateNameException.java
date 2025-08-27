package com.dnd.moddo.domain.appointmentMember.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class AppointmentMemberDuplicateNameException extends ModdoException {
	public AppointmentMemberDuplicateNameException() {
		super(HttpStatus.CONFLICT, "중복된 참여자의 이름은 저장할 수 없습니다.");
	}
}
