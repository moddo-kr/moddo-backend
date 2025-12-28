package com.dnd.moddo.event.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class PaymentConcurrencyException extends ModdoException {
	public PaymentConcurrencyException() {
		super(HttpStatus.BAD_REQUEST, "다른 사용자가 상태를 갱신했습니다");
	}
}
