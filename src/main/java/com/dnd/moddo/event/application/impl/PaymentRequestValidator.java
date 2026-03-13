package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Component;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.domain.paymentRequest.exception.DuplicatePendingPaymentRequestException;
import com.dnd.moddo.event.domain.paymentRequest.exception.ManagerPaymentRequestNotAllowedException;
import com.dnd.moddo.event.domain.paymentRequest.exception.PaymentRequestAlreadyApprovedException;
import com.dnd.moddo.event.domain.paymentRequest.exception.PaymentRequestNotPendingException;
import com.dnd.moddo.event.domain.paymentRequest.exception.PaymentRequestUnauthorizedException;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRequestValidator {
	private final PaymentRequestRepository paymentRequestRepository;

	public void validateCreateRequest(Long settlementId, Member requestMember) {
		validateIsManager(requestMember);
		validateDuplicateRequest(settlementId, requestMember.getId());
		validateAlreadyApprovedRequest(settlementId, requestMember.getId());
	}

	public void validateProcessRequest(PaymentRequest paymentRequest, Long userId) {
		validatePendingStatus(paymentRequest);
		validateTargetUser(paymentRequest, userId);
	}

	private void validateDuplicateRequest(Long settlementId, Long requestMemberId) {
		boolean exists = paymentRequestRepository.existsBySettlementIdAndRequestMemberIdAndStatus(
			settlementId,
			requestMemberId,
			PaymentRequestStatus.PENDING
		);

		if (exists) {
			throw new DuplicatePendingPaymentRequestException(settlementId, requestMemberId);
		}
	}

	private void validateAlreadyApprovedRequest(Long settlementId, Long requestMemberId) {
		boolean exists = paymentRequestRepository.existsBySettlementIdAndRequestMemberIdAndStatus(
			settlementId,
			requestMemberId,
			PaymentRequestStatus.APPROVED
		);

		if (exists) {
			throw new PaymentRequestAlreadyApprovedException(settlementId, requestMemberId);
		}
	}

	private void validateIsManager(Member requestMember) {
		if (requestMember.isManager()) {
			throw new ManagerPaymentRequestNotAllowedException(requestMember.getId());
		}
	}

	private void validatePendingStatus(PaymentRequest paymentRequest) {
		if (paymentRequest.getStatus() != PaymentRequestStatus.PENDING) {
			throw new PaymentRequestNotPendingException(paymentRequest.getId(), paymentRequest.getStatus());
		}
	}

	private void validateTargetUser(PaymentRequest paymentRequest, Long userId) {
		if (!paymentRequest.getTargetUser().getId().equals(userId)) {
			throw new PaymentRequestUnauthorizedException(paymentRequest.getId(), userId);
		}
	}
}
