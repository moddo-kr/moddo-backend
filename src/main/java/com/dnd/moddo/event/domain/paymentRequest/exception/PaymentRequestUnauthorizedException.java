package com.dnd.moddo.event.domain.paymentRequest.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class PaymentRequestUnauthorizedException extends ModdoException {
	public PaymentRequestUnauthorizedException(Long paymentRequestId, Long userId) {
		super(
			HttpStatus.FORBIDDEN,
			"해당 입금 확인 요청을 처리할 권한이 없습니다. (PaymentRequest ID: " + paymentRequestId + ", User ID: " + userId + ")"
		);
	}
}
