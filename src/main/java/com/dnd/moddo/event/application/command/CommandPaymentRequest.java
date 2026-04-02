package com.dnd.moddo.event.application.command;

import org.springframework.stereotype.Service;

import com.dnd.moddo.common.cache.CacheEvictor;
import com.dnd.moddo.event.application.impl.SettlementCompletionProcessor;
import com.dnd.moddo.event.application.impl.PaymentRequestCreator;
import com.dnd.moddo.event.application.impl.PaymentRequestUpdater;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.presentation.response.PaymentRequestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandPaymentRequest {
	private final CacheEvictor cacheEvictor;
	private final PaymentRequestCreator paymentRequestCreator;
	private final PaymentRequestUpdater paymentRequestUpdater;
	private final SettlementCompletionProcessor settlementCompletionProcessor;

	public PaymentRequestResponse createPaymentRequest(Long settlementId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestCreator.createPaymentRequest(settlementId, userId);
		return PaymentRequestResponse.of(paymentRequest);
	}

	public PaymentRequestResponse approvePaymentRequest(Long paymentRequestId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestUpdater.approvePaymentRequest(paymentRequestId, userId);
		settlementCompletionProcessor.completeIfAllPaid(paymentRequest.getSettlementId());
		cacheEvictor.evictMembers(paymentRequest.getSettlementId());
		cacheEvictor.evictSettlementListsBySettlement(paymentRequest.getSettlementId());
		return PaymentRequestResponse.of(paymentRequest);
	}

	public PaymentRequestResponse rejectPaymentRequest(Long paymentRequestId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestUpdater.rejectPaymentRequest(paymentRequestId, userId);
		return PaymentRequestResponse.of(paymentRequest);
	}
}
