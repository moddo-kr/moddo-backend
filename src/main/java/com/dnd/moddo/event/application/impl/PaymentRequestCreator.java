package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;
import com.dnd.moddo.user.application.impl.UserReader;
import com.dnd.moddo.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentRequestCreator {
	private final PaymentRequestRepository paymentRequestRepository;
	private final MemberReader memberReader;
	private final SettlementReader settlementReader;
	private final UserReader userReader;
	private final PaymentRequestValidator paymentRequestValidator;

	public PaymentRequest createPaymentRequest(Long settlementId, Long userId) {
		Member requestMember = memberReader.findBySettlementIdAndUserId(settlementId, userId);
		Settlement settlement = settlementReader.read(settlementId);

		paymentRequestValidator.validateCreateRequest(settlementId, requestMember);

		User targetUser = userReader.read(settlement.getWriter());

		PaymentRequest paymentRequest = PaymentRequest.builder()
			.settlement(settlement)
			.requestMember(requestMember)
			.targetUser(targetUser)
			.build();

		return paymentRequestRepository.save(paymentRequest);
	}
}
