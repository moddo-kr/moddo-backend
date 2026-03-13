package com.dnd.moddo.event.domain.paymentRequest.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class PaymentRequestNotFoundException extends ModdoException {
	public PaymentRequestNotFoundException(Long paymentRequestId) {
		super(HttpStatus.NOT_FOUND, "해당 입금 확인 요청을 찾을 수 없습니다. (PaymentRequest ID: " + paymentRequestId + ")");
	}
}
