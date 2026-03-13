package com.dnd.moddo.event.presentation.response;

import java.util.List;

public record PaymentRequestsResponse(
	List<PaymentRequestItemResponse> paymentRequests
) {
	public static PaymentRequestsResponse of(List<PaymentRequestItemResponse> paymentRequests) {
		return new PaymentRequestsResponse(paymentRequests);
	}
}
