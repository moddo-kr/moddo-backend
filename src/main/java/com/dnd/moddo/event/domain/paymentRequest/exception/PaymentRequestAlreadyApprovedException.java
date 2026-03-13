package com.dnd.moddo.event.domain.paymentRequest.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class PaymentRequestAlreadyApprovedException extends ModdoException {
	public PaymentRequestAlreadyApprovedException(Long settlementId, Long requestMemberId) {
		super(
			HttpStatus.BAD_REQUEST,
			"이미 완료된 입금 확인 요청이 있습니다. (Settlement ID: " + settlementId + ", Member ID: " + requestMemberId + ")"
		);
	}
}
