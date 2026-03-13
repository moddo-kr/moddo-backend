package com.dnd.moddo.event.domain.paymentRequest.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class DuplicatePendingPaymentRequestException extends ModdoException {
	public DuplicatePendingPaymentRequestException(Long settlementId, Long requestMemberId) {
		super(
			HttpStatus.BAD_REQUEST,
			"이미 처리 대기 중인 입금 확인 요청이 있습니다. (Settlement ID: " + settlementId + ", Member ID: " + requestMemberId + ")"
		);
	}
}
