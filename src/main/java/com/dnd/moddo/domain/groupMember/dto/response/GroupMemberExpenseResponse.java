package com.dnd.moddo.domain.groupMember.dto.response;

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
	List<MemberExpenseDetailResponse> expenses
) {
	public static GroupMemberExpenseResponse of(GroupMember groupMember, List<MemberExpenseDetailResponse> expenses) {
		return GroupMemberExpenseResponse.builder()
			.id(groupMember.getId())
			.role(groupMember.getRole())
			.name(groupMember.getName())
			.expenses(expenses).build();
	}
}
