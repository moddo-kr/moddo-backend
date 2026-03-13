package com.dnd.moddo.event.application.query;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.PaymentRequestReader;
import com.dnd.moddo.event.presentation.response.PaymentRequestsResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryPaymentRequestService {
	private final PaymentRequestReader paymentRequestReader;

	public PaymentRequestsResponse findByTargetUserId(Long targetUserId) {
		return paymentRequestReader.findByTargetUserId(targetUserId);
	}
}
