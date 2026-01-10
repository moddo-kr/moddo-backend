package com.dnd.moddo.event.application.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.ExpenseReader;
import com.dnd.moddo.event.application.impl.MemberExpenseReader;
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.presentation.response.MemberExpenseDetailResponse;
import com.dnd.moddo.event.presentation.response.MemberExpenseItemResponse;
import com.dnd.moddo.event.presentation.response.MemberExpenseResponse;
import com.dnd.moddo.event.presentation.response.MembersExpenseResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryMemberExpenseService {
	private final MemberExpenseReader memberExpenseReader;
	private final MemberReader memberReader;
	private final ExpenseReader expenseReader;

	public List<MemberExpenseResponse> findAllByExpenseId(
		Long expenseId) {
		List<MemberExpense> memberExpenses = memberExpenseReader.findAllByExpenseId(expenseId);
		return memberExpenses.stream()
			.map(MemberExpenseResponse::of)
			.toList();
	}

	public MembersExpenseResponse findMemberExpenseDetailsBySettlementId(Long settlementId) {
		List<Member> members = memberReader.findAllBySettlementId(settlementId);

		Map<Long, Member> appointmentMemberById = convertAppointmentMembersToMap(members);

		Map<Long, List<MemberExpense>> memberExpenses = memberExpenseReader.findAllByAppointMemberIds(
				appointmentMemberById.keySet().stream().toList())
			.stream()
			.collect(Collectors.groupingBy(me -> me.getMember().getId()));
		;

		List<Expense> expenses = expenseReader.findAllBySettlementId(settlementId);

		List<MemberExpenseItemResponse> responses = appointmentMemberById.keySet()
			.stream()
			.map(
				key -> findMemberExpenseDetailByAppointmentMember(appointmentMemberById.get(key),
					memberExpenses.get(key),
					expenses)
			)
			.filter(Objects::nonNull)
			.toList();

		return new MembersExpenseResponse(responses);
	}

	private Map<Long, Member> convertAppointmentMembersToMap(List<Member> members) {
		return members.stream()
			.collect(Collectors.toMap(Member::getId, appointmentMember -> appointmentMember,
				(existing, replacement) -> existing,
				LinkedHashMap::new)
			);
	}

	private MemberExpenseItemResponse findMemberExpenseDetailByAppointmentMember(
		Member member, List<MemberExpense> memberExpenses, List<Expense> expenses) {

		if (memberExpenses == null) {
			return MemberExpenseItemResponse.of(member, 0L, new ArrayList<>());
		}

		List<MemberExpenseDetailResponse> memberExpenseDetails = mapToMemberExpenseDetails(memberExpenses, expenses);
		Long totalAmount = calculateTotalAmount(memberExpenses);

		return MemberExpenseItemResponse.of(member, totalAmount, memberExpenseDetails);
	}

	private Long calculateTotalAmount(List<MemberExpense> memberExpenses) {
		return memberExpenses.stream()
			.mapToLong(MemberExpense::getAmount)
			.sum();
	}

	private List<MemberExpenseDetailResponse> mapToMemberExpenseDetails(
		List<MemberExpense> memberExpenses, List<Expense> expenses) {

		Map<Long, MemberExpense> expenseToMemberExpenseMap = memberExpenses.stream()
			.collect(Collectors.toMap(MemberExpense::getExpenseId, me -> me));

		return expenses.stream()
			.map(expense -> {
				MemberExpense memberExpense = expenseToMemberExpenseMap.get(expense.getId());
				return (memberExpense != null) ? MemberExpenseDetailResponse.of(expense, memberExpense) : null;
			})
			.filter(Objects::nonNull)
			.toList();
	}

	public Map<Long, List<String>> getMemberNamesByExpenseIds(List<Long> expenseIds) {
		List<MemberExpense> memberExpenses = memberExpenseReader.findAllByExpenseIds(expenseIds);

		return memberExpenses.stream()
			.collect(Collectors.groupingBy(
				MemberExpense::getExpenseId,
				Collectors.mapping(this::formatMemberName, Collectors.toUnmodifiableList())
			));
	}

	private String formatMemberName(MemberExpense me) {
		String name = me.getMember().getName();
		return me.getMember().isManager() ? name + "(총무)" : name;
	}
}
