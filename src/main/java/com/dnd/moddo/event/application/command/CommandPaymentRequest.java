package com.dnd.moddo.event.application.command;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.PaymentRequestCreator;
import com.dnd.moddo.event.application.impl.PaymentRequestUpdater;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.presentation.response.PaymentRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandPaymentRequest {
	private final PaymentRequestCreator paymentRequestCreator;
	private final PaymentRequestUpdater paymentRequestUpdater;

	public PaymentRequestResponse createPaymentRequest(Long settlementId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestCreator.createPaymentRequest(settlementId, userId);
		return PaymentRequestResponse.of(paymentRequest);
	}

	public PaymentRequestResponse approvePaymentRequest(Long paymentRequestId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestUpdater.approvePaymentRequest(paymentRequestId, userId);
		return PaymentRequestResponse.of(paymentRequest);
	}

	public PaymentRequestResponse rejectPaymentRequest(Long paymentRequestId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestUpdater.rejectPaymentRequest(paymentRequestId, userId);
		return PaymentRequestResponse.of(paymentRequest);
	}
}
