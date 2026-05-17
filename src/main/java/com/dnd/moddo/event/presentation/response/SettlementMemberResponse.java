package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;

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
	Long paymentRequestId
) {

	public static SettlementMemberResponse of(Member member, Long paymentRequestId) {
		return SettlementMemberResponse.builder()
			.id(member.getId())
			.name(member.getName())
			.role(member.getRole())
			.userId(member.getUserId())
			.isPaid(member.isPaid())
			.paidAt(member.getPaidAt())
			.paymentRequestId(paymentRequestId)
			.profile(member.getProfileUrl())
			.build();
	}
}
