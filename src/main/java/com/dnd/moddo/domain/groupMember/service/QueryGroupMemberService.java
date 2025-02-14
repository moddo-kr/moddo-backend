package com.dnd.moddo.domain.groupMember.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryGroupMemberService {
	private final GroupMemberReader groupMemberReader;
	private final MemberExpenseReader memberExpenseReader;
	private final ExpenseReader expenseReader;

	public GroupMembersResponse findAll(Long groupId) {
		List<GroupMember> members = groupMemberReader.findAllByGroupId(groupId);
		return GroupMembersResponse.of(members);
	}

}
