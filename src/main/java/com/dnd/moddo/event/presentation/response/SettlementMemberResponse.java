package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;

import lombok.Builder;

@Builder
public record SettlementMemberResponse(
	Long id,
	ExpenseRole role,
	String name,
	String profile,
	Long userId,
	Boolean isPaid,
	LocalDateTime paidAt,
	Long paymentRequestId,
	PaymentRequestStatus paymentRequestStatus,
	String paymentRequestStatusLabel
) {

	public static SettlementMemberResponse of(Member member, PaymentRequestSummaryResponse paymentRequest) {
		return SettlementMemberResponse.builder()
			.id(member.getId())
			.name(member.getName())
			.role(member.getRole())
			.userId(member.getUserId())
			.isPaid(member.isPaid())
			.paidAt(member.getPaidAt())
			.paymentRequestId(paymentRequest == null ? null : paymentRequest.id())
			.paymentRequestStatus(paymentRequest == null ? null : paymentRequest.status())
			.paymentRequestStatusLabel(paymentRequest == null ? null : paymentRequest.statusLabel())
			.profile(member.getProfileUrl())
			.build();
	}
}
