package com.dnd.moddo.domain.groupMember.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseDetailResponse;

import lombok.Builder;

@Builder
public record GroupMemberExpenseResponse(
	Long id,
	ExpenseRole role,
	String name,
	Long totalAmount,
	boolean isPaid,
	LocalDateTime paidAt,
	List<MemberExpenseDetailResponse> expenses
) {
	public static GroupMemberExpenseResponse of(GroupMember groupMember, Long totalAmount,
		List<MemberExpenseDetailResponse> expenses) {
		return GroupMemberExpenseResponse.builder()
			.id(groupMember.getId())
			.role(groupMember.getRole())
			.name(groupMember.getName())
			.totalAmount(totalAmount)
			.isPaid(groupMember.isPaid())
			.paidAt(groupMember.getPaidAt())
			.expenses(expenses).build();
	}
}
