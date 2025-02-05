package com.dnd.moddo.domain.memberExpense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpensesResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseCreator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandMemberExpenseService {
	private final MemberExpenseCreator memberExpenseCreator;
	private final GroupMemberReader groupMemberReader;

	public MemberExpensesResponse createMemberExpenses(Expense expense, List<MemberExpenseRequest> requests) {
		return MemberExpensesResponse.of(
			requests.stream()
				.map(rq -> createMemberExpense(expense, rq))
				.toList()
		);
	}

	public MemberExpense createMemberExpense(Expense expense, MemberExpenseRequest request) {
		GroupMember groupMember = groupMemberReader.getByGroupMemberId(request.memberId());
		return memberExpenseCreator.create(expense, groupMember, request);
	}
}
