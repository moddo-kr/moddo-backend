package com.dnd.moddo.domain.memberExpense.dto.request;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;

public record MemberExpenseRequest(Long memberId, Long amount) {
	public MemberExpense toEntity(Expense expense, GroupMember groupMember) {
		return MemberExpense.builder()
			.expense(expense)
			.groupMember(groupMember)
			.amount(amount())
			.build();
	}
}
