package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentRequestUpdater {
	private final PaymentRequestRepository paymentRequestRepository;
	private final PaymentRequestValidator paymentRequestValidator;
	private final MemberUpdater memberUpdater;

	@Transactional
	public PaymentRequest approvePaymentRequest(Long paymentRequestId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestRepository.getById(paymentRequestId);
		paymentRequestValidator.validateProcessRequest(paymentRequest, userId);
		memberUpdater.updatePaymentStatus(paymentRequest.getRequestMemberId(), true);
		paymentRequest.approve();
		return paymentRequest;
	}

	@Transactional
	public PaymentRequest rejectPaymentRequest(Long paymentRequestId, Long userId) {
		PaymentRequest paymentRequest = paymentRequestRepository.getById(paymentRequestId);
		paymentRequestValidator.validateProcessRequest(paymentRequest, userId);
		paymentRequest.reject();
		return paymentRequest;
	}
}
