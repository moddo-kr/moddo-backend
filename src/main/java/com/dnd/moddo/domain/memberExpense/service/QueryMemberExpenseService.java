package com.dnd.moddo.domain.memberExpense.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberExpenseResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersExpenseResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseDetailResponse;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryMemberExpenseService {
	private final MemberExpenseReader memberExpenseReader;
	private final GroupMemberReader groupMemberReader;
	private final ExpenseReader expenseReader;

	public List<MemberExpenseResponse> findAllByExpenseId(Long expenseId) {
		List<MemberExpense> memberExpenses = memberExpenseReader.findAllByExpenseId(expenseId);
		return memberExpenses.stream()
			.map(MemberExpenseResponse::of)
			.toList();
	}

	public GroupMembersExpenseResponse findMemberExpenseDetailsByGroupId(Long groupId) {
		List<GroupMember> groupMembers = groupMemberReader.findAllByGroupId(groupId);

		Map<Long, GroupMember> groupMemberById = convertGroupMembersToMap(groupMembers);

		Map<Long, List<MemberExpense>> memberExpenses = memberExpenseReader.findAllByGroupMemberIds(
			groupMemberById.keySet().stream().toList());

		List<Expense> expenses = expenseReader.findAllByGroupId(groupId);

		List<GroupMemberExpenseResponse> responses = groupMemberById.keySet()
			.stream()
			.map(key -> {
					if (memberExpenses.get(key) == null)
						return null;
					return findMemberExpenseDetailByGroupMember(groupMemberById.get(key), memberExpenses.get(key),
						expenses);
				}
			)
			.filter(Objects::nonNull)
			.toList();

		return new GroupMembersExpenseResponse(responses);
	}

	private Map<Long, GroupMember> convertGroupMembersToMap(List<GroupMember> groupMembers) {
		return groupMembers.stream()
			.collect(Collectors.toMap(GroupMember::getId, groupMember -> groupMember,
				(existing, replacement) -> existing,
				LinkedHashMap::new)
			);
	}

	private GroupMemberExpenseResponse findMemberExpenseDetailByGroupMember(GroupMember groupMember,
		List<MemberExpense> memberExpenses, List<Expense> expenses) {

		Map<Long, MemberExpense> expenseToMemberExpenseMap = memberExpenses.stream()
			.collect(Collectors.toMap(MemberExpense::getExpenseId, me -> me));

		List<MemberExpenseDetailResponse> memberExpenseDetails = expenses.stream()
			.map(expense -> {
				MemberExpense memberExpense = expenseToMemberExpenseMap.get(expense.getId());
				return (memberExpense != null) ? MemberExpenseDetailResponse.of(expense, memberExpense) : null;
			})
			.filter(Objects::nonNull)
			.toList();

		return GroupMemberExpenseResponse.of(groupMember, memberExpenseDetails);
	}
}
