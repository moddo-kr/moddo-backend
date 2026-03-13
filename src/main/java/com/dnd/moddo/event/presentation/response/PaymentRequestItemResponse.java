package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

public record PaymentRequestItemResponse(
	LocalDateTime requestedAt,
	Long paymentRequestId,
	Long memberId,
	String name,
	String profileUrl,
	Long totalAmount
) {
}
