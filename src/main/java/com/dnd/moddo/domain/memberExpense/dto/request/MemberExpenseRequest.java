package com.dnd.moddo.domain.memberExpense.dto.request;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public record MemberExpenseRequest(Long memberId, Long amount) {
	public MemberExpense toEntity(Long expenseId, GroupMember groupMember) {
		return MemberExpense.builder()
			.expenseId(expenseId)
			.groupMember(groupMember)
			.amount(amount())
			.build();
	}
}
