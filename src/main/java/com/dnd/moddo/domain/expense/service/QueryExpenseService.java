package com.dnd.moddo.domain.expense.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberExpenseResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersExpenseResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseDetailResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.QueryMemberExpenseService;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryExpenseService {
	private final ExpenseReader expenseReader;
	private final GroupMemberReader groupMemberReader;
	private final MemberExpenseReader memberExpenseReader;
	private final QueryMemberExpenseService queryMemberExpenseService;

	public ExpensesResponse findAllByGroupId(Long groupId) {
		List<Expense> expenses = expenseReader.findAllByGroupId(groupId);
		return new ExpensesResponse(
			expenses.stream()
				.map(expense ->
					ExpenseResponse.of(expense, queryMemberExpenseService.findAllByExpenseId(expense.getId()))
				).toList()
		);
	}

	public ExpenseResponse findOneByExpenseId(Long expenseId) {
		Expense expense = expenseReader.findOneByExpenseId(expenseId);
		return ExpenseResponse.of(expense);
	}

	public GroupMembersExpenseResponse findSettlementByGroupId(Long groupId) {
		List<GroupMember> groupMembers = groupMemberReader.findAllByGroupId(groupId);

		Map<Long, GroupMember> groupMemberById = mapGroupMembers(groupMembers);

		Map<Long, List<MemberExpense>> memberExpenses = memberExpenseReader.findAllByGroupMemberIds(
			groupMemberById.keySet().stream().toList());

		List<Expense> expenses = expenseReader.findAllByGroupId(groupId);

		List<GroupMemberExpenseResponse> responses = groupMemberById.keySet()
			.stream()
			.map(key -> {
					if (memberExpenses.get(key) == null)
						return null;
					return findSettlementByGroupMember(groupMemberById.get(key), memberExpenses.get(key), expenses);
				}
			)
			.filter(Objects::nonNull)
			.toList();

		return new GroupMembersExpenseResponse(responses);
	}

	// 그룹 멤버들을 id별로 Map으로 매핑
	private Map<Long, GroupMember> mapGroupMembers(List<GroupMember> groupMembers) {
		return groupMembers.stream()
			.collect(Collectors.toMap(GroupMember::getId, groupMember -> groupMember,
				(existing, replacement) -> existing,
				LinkedHashMap::new)
			);
	}

	private GroupMemberExpenseResponse findSettlementByGroupMember(GroupMember groupMember,
		List<MemberExpense> memberExpenses, List<Expense> expenses) {

		// Map으로 변환하여 빠른 조회 가능
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
