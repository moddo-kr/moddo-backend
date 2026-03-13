package com.dnd.moddo.event.domain.paymentRequest.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class ManagerPaymentRequestNotAllowedException extends ModdoException {
	public ManagerPaymentRequestNotAllowedException(Long requestMemberId) {
		super(HttpStatus.FORBIDDEN, "정산 담당자는 입금 확인 요청을 보낼 수 없습니다.");
	}
}
