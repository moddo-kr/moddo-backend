package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.paymentRequest.PaymentRequestStatus;

import lombok.Builder;

@Builder
public record MemberExpenseItemResponse(
	Long id,
	ExpenseRole role,
	String name,
	Long totalAmount,
	String profile,
	boolean isPaid,
	LocalDateTime paidAt,
	Long paymentRequestId,
	PaymentRequestStatus paymentRequestStatus,
	String paymentRequestStatusLabel,
	List<MemberExpenseDetailResponse> expenses
) {
	public static MemberExpenseItemResponse of(Member member, Long totalAmount,
		List<MemberExpenseDetailResponse> expenses, PaymentRequestSummaryResponse paymentRequest, boolean isManager) {
		return MemberExpenseItemResponse.builder()
			.id(member.getId())
			.role(member.getRole())
			.name(member.getName())
			.totalAmount(totalAmount)
			.profile(member.getProfileUrl())
			.isPaid(member.isPaid())
			.paidAt(member.getPaidAt())
			.paymentRequestId(paymentRequest == null || !isManager ? null : paymentRequest.id())
			.paymentRequestStatus(paymentRequest == null ? null : paymentRequest.status())
			.paymentRequestStatusLabel(paymentRequest == null ? null : paymentRequest.statusLabel())
			.expenses(expenses).build();
	}
}
