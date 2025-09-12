package com.dnd.moddo.domain.appointmentMember.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class AppointmentMemberNotFoundException extends ModdoException {
	public AppointmentMemberNotFoundException(Long appointmentMemberId) {
		super(HttpStatus.NOT_FOUND, "해당 참여자를 찾을 수 없습니다. (AppointmentMember ID: " + appointmentMemberId + ")");
	}
}
