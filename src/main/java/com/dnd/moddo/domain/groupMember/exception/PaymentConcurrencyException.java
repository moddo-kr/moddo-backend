package com.dnd.moddo.domain.groupMember.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.global.exception.ModdoException;

public class PaymentConcurrencyException extends ModdoException {
	public PaymentConcurrencyException() {
		super(HttpStatus.BAD_REQUEST, "다른 사용자가 상태를 갱신했습니다");
	}
}
