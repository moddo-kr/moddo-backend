package com.dnd.moddo.event.domain.paymentRequest.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;

public class PaymentRequestNotPendingException extends ModdoException {
	public PaymentRequestNotPendingException(Long paymentRequestId, PaymentRequestStatus status) {
		super(
			HttpStatus.BAD_REQUEST,
			"처리 대기 상태의 입금 확인 요청만 처리할 수 있습니다. (PaymentRequest ID: " + paymentRequestId + ", Status: " + status + ")"
		);
	}
}
