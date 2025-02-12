package com.dnd.moddo.domain.groupMember.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberExpenseResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersExpenseResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseDetailResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
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

	public GroupMembersExpenseResponse findSettlementByGroupId(Long groupId) {
		List<GroupMember> groupMembers = groupMemberReader.findAllByGroupId(groupId);

		Map<Long, GroupMember> groupMemberById = groupMembers.stream()
			.collect(Collectors.toMap(GroupMember::getId, groupMember -> groupMember));

		Map<Long, List<MemberExpense>> memberExpenses = memberExpenseReader.findAllByGroupMemberIds(
			new ArrayList<>(groupMemberById.keySet()));

		List<Expense> expenses = expenseReader.findAllByGroupId(groupId);

		List<GroupMemberExpenseResponse> responses = groupMemberById.keySet()
			.stream()
			.map(key -> {
				if (memberExpenses.get(key) == null)
					return null;
				return findSettlementByGroupMember(groupMemberById.get(key), memberExpenses.get(key), expenses);
			})
			.filter(Objects::nonNull)
			.toList();

		return new GroupMembersExpenseResponse(responses);
	}

	private GroupMemberExpenseResponse findSettlementByGroupMember(GroupMember groupMember,
		List<MemberExpense> memberExpenses,
		List<Expense> expenses) {

		List<MemberExpenseDetailResponse> memberExepsneDetail = expenses.stream()
			.map(e -> {
				MemberExpense memberExpense = memberExpenses.stream()
					.filter(me -> me.isExpenseMatched(e))
					.findFirst()
					.orElse(null);

				return memberExpense != null
					? MemberExpenseDetailResponse.of(e, memberExpense)
					: null;
			}).filter(Objects::nonNull)
			.toList();

		return GroupMemberExpenseResponse.of(groupMember, memberExepsneDetail);
	}
}
