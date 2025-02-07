package com.dnd.moddo.domain.memberExpense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseCreator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandMemberExpenseService {
	private final GroupMemberReader groupMemberReader;
	private final MemberExpenseCreator memberExpenseCreator;

	public List<MemberExpenseResponse> create(Expense expense, List<MemberExpenseRequest> memberExpenses) {
		return memberExpenses.stream()
			.map(m -> {
				GroupMember groupMember = groupMemberReader.findByGroupMemberId(m.memberId());
				return MemberExpenseResponse.of(memberExpenseCreator.create(expense, groupMember, m));
			}).toList();

	}
}
