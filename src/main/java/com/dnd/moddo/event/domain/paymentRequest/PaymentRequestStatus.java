package com.dnd.moddo.event.domain.paymentRequest;

import lombok.Getter;

@Getter
public enum PaymentRequestStatus {
	PENDING("확인중"),
	APPROVED("승인완료"),
	REJECTED("거절");

	private final String label;

	PaymentRequestStatus(String label) {
		this.label = label;
	}
}
