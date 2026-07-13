package com.dnd.moddo.event.application.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequest;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;
import com.dnd.moddo.event.infrastructure.PaymentRequestRepository;
import com.dnd.moddo.event.presentation.response.PaymentRequestItemResponse;
import com.dnd.moddo.event.presentation.response.PaymentRequestsResponse;
import com.dnd.moddo.event.presentation.response.PaymentRequestSummaryResponse;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentRequestReader {
	private final PaymentRequestRepository paymentRequestRepository;
	private final MemberExpenseReader memberExpenseReader;
	private final MemberReader memberReader;

	public PaymentRequestsResponse findByTargetUserId(Long targetUserId) {
		List<PaymentRequest> paymentRequests = paymentRequestRepository.findByTargetUserId(targetUserId)
			.stream().filter(paymentRequest -> paymentRequest.getStatus() == PaymentRequestStatus.PENDING)
			.toList();

		List<Long> memberIds = paymentRequests.stream()
			.map(PaymentRequest::getRequestMemberId)
			.distinct()
			.toList();

		Map<Long, Long> amountByMemberId = memberExpenseReader.findAllByMemberIds(memberIds).stream()
			.collect(Collectors.groupingBy(
				MemberExpense::getMemberId,
				Collectors.summingLong(me -> me.getAmount() != null ? me.getAmount() : 0L)
			));

		List<PaymentRequestItemResponse> responses = paymentRequests.stream()
			.map(paymentRequest -> {
				Member member = paymentRequest.getRequestMember();
				Long memberId = member.getId();

				return new PaymentRequestItemResponse(
					paymentRequest.getRequestedAt(),
					paymentRequest.getId(),
					memberId,
					member.getName(),
					member.getProfileUrl(),
					amountByMemberId.getOrDefault(memberId, 0L)
				);
			})
			.sorted(Comparator.comparing(PaymentRequestItemResponse::requestedAt).reversed())
			.toList();

		return PaymentRequestsResponse.of(responses);
	}

	public boolean existsBySettlementId(Long settlementId) {
		return paymentRequestRepository.existsBySettlementId(settlementId)
			|| memberReader.existsPaidParticipant(settlementId);
	}

	public Map<Long, PaymentRequestSummaryResponse> findLatestRequestByMemberId(Long settlementId) {
		return paymentRequestRepository.findBySettlementIdOrderByRequestedAtDesc(settlementId)
			.stream()
			.collect(Collectors.toMap(
				PaymentRequest::getRequestMemberId,
				PaymentRequestSummaryResponse::of,
				(first, duplicate) -> first
			));
	}

}
