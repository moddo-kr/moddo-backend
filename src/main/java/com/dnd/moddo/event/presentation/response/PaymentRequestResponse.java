package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;

public record PaymentRequestResponse(
	Long id,
	Long settlementId,
	Long requestMemberId,
	Long targetUserId,
	LocalDateTime requestedAt,
	LocalDateTime processedAt,
	PaymentRequestStatus status
) {
	public static PaymentRequestResponse of(PaymentRequest paymentRequest) {
		return new PaymentRequestResponse(
			paymentRequest.getId(),
			paymentRequest.getSettlementId(),
			paymentRequest.getRequestMemberId(),
			paymentRequest.getTargetUserId(),
			paymentRequest.getRequestedAt(),
			paymentRequest.getProcessedAt(),
			paymentRequest.getStatus()
		);
	}
}
