package com.dnd.moddo.domain.memberExpense.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberExpenseResponse;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMembersExpenseResponse;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberReader;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseDetailResponse;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryMemberExpenseService {
	private final MemberExpenseReader memberExpenseReader;
	private final AppointmentMemberReader appointmentMemberReader;
	private final ExpenseReader expenseReader;

	public List<MemberExpenseResponse> findAllByExpenseId(Long expenseId) {
		List<MemberExpense> memberExpenses = memberExpenseReader.findAllByExpenseId(expenseId);
		return memberExpenses.stream()
			.map(MemberExpenseResponse::of)
			.toList();
	}

	public AppointmentMembersExpenseResponse findMemberExpenseDetailsBySettlementId(Long settlementId) {
		List<AppointmentMember> appointmentMembers = appointmentMemberReader.findAllBySettlementId(settlementId);

		Map<Long, AppointmentMember> appointmentMemberById = convertAppointmentMembersToMap(appointmentMembers);

		Map<Long, List<MemberExpense>> memberExpenses = memberExpenseReader.findAllByAppointMemberIds(
				appointmentMemberById.keySet().stream().toList())
			.stream()
			.collect(Collectors.groupingBy(me -> me.getAppointmentMember().getId()));
		;

		List<Expense> expenses = expenseReader.findAllBySettlementId(settlementId);

		List<AppointmentMemberExpenseResponse> responses = appointmentMemberById.keySet()
			.stream()
			.map(
				key -> findMemberExpenseDetailByAppointmentMember(appointmentMemberById.get(key),
					memberExpenses.get(key),
					expenses)
			)
			.filter(Objects::nonNull)
			.toList();

		return new AppointmentMembersExpenseResponse(responses);
	}

	private Map<Long, AppointmentMember> convertAppointmentMembersToMap(List<AppointmentMember> appointmentMembers) {
		return appointmentMembers.stream()
			.collect(Collectors.toMap(AppointmentMember::getId, appointmentMember -> appointmentMember,
				(existing, replacement) -> existing,
				LinkedHashMap::new)
			);
	}

	private AppointmentMemberExpenseResponse findMemberExpenseDetailByAppointmentMember(
		AppointmentMember appointmentMember, List<MemberExpense> memberExpenses, List<Expense> expenses) {

		if (memberExpenses == null) {
			return AppointmentMemberExpenseResponse.of(appointmentMember, 0L, new ArrayList<>());
		}

		List<MemberExpenseDetailResponse> memberExpenseDetails = mapToMemberExpenseDetails(memberExpenses, expenses);
		Long totalAmount = calculateTotalAmount(memberExpenses);

		return AppointmentMemberExpenseResponse.of(appointmentMember, totalAmount, memberExpenseDetails);
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
		String name = me.getAppointmentMember().getName();
		return me.getAppointmentMember().isManager() ? name + "(총무)" : name;
	}
}
