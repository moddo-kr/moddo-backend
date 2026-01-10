package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;

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
	List<MemberExpenseDetailResponse> expenses
) {
	public static MemberExpenseItemResponse of(Member member, Long totalAmount,
		List<MemberExpenseDetailResponse> expenses) {
		return MemberExpenseItemResponse.builder()
			.id(member.getId())
			.role(member.getRole())
			.name(member.getName())
			.totalAmount(totalAmount)
			.profile(member.getProfileUrl())
			.isPaid(member.isPaid())
			.paidAt(member.getPaidAt())
			.expenses(expenses).build();
	}
}
