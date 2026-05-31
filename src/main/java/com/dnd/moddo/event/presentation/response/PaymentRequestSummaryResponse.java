package com.dnd.moddo.event.presentation.response;

import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;

public record PaymentRequestSummaryResponse(
	Long id,
	PaymentRequestStatus status,
	String statusLabel
) {
	public static PaymentRequestSummaryResponse of(PaymentRequest paymentRequest) {
		return new PaymentRequestSummaryResponse(
			paymentRequest.getId(),
			paymentRequest.getStatus(),
			paymentRequest.getStatus().getLabel()
		);
	}
}
