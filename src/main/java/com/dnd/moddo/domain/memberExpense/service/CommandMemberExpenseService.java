package com.dnd.moddo.domain.memberExpense.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseCreator;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseDeleter;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseUpdater;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandMemberExpenseService {
	private final AppointmentMemberReader appointmentMemberReader;
	private final MemberExpenseCreator memberExpenseCreator;
	private final MemberExpenseReader memberExpenseReader;
	private final MemberExpenseUpdater memberExpenseUpdater;
	private final MemberExpenseDeleter memberExpenseDeleter;

	public List<MemberExpenseResponse> create(Long expenseId, List<MemberExpenseRequest> request) {
		return request.stream()
			.map(m -> {
				AppointmentMember appointmentMember = appointmentMemberReader.findByAppointmentMemberId(m.id());
				return MemberExpenseResponse.of(memberExpenseCreator.create(expenseId, appointmentMember, m));
			}).toList();

	}

	public List<MemberExpenseResponse> update(Long expenseId, List<MemberExpenseRequest> requests) {
		//1. 지출내역에 딸린 멤버별 지출내역을 모두 가져온다
		List<MemberExpense> memberExpenses = memberExpenseReader.findAllByExpenseId(expenseId);

		Map<Long, MemberExpense> existingMemberExpenses = memberExpenses.stream()
			.collect(Collectors.toMap(me -> me.getAppointmentMember().getId(), me -> me));

		//2. 기존에 있던 멤버가 요청에 없는 경우 삭제한다.
		deleteNonIncludedMemberExpenses(requests, memberExpenses);

		// 3. 요청된 멤버별 지출 내역을 처리한다.
		return requests.stream()
			.map(request -> handleMemberExpenseUpdateOrCreate(expenseId, existingMemberExpenses, request))
			.collect(Collectors.toList());
	}

	private MemberExpenseResponse handleMemberExpenseUpdateOrCreate(Long expenseId,
		Map<Long, MemberExpense> existingMemberExpenses,
		MemberExpenseRequest request) {

		Long groupMemberId = request.id();
		MemberExpenseResponse response;

		if (existingMemberExpenses.containsKey(groupMemberId)) {
			// 1. 기존 멤버가 있으면 업데이트
			MemberExpense existingMemberExpense = existingMemberExpenses.get(groupMemberId);
			memberExpenseUpdater.update(existingMemberExpense, request);
			response = MemberExpenseResponse.of(existingMemberExpense);
		} else {
			// 2. 없는 멤버면 추가
			AppointmentMember appointmentMember = appointmentMemberReader.findByAppointmentMemberId(groupMemberId);
			MemberExpense newMemberExpense = memberExpenseCreator.create(expenseId, appointmentMember, request);
			response = MemberExpenseResponse.of(newMemberExpense);
		}

		return response;
	}

	private void deleteNonIncludedMemberExpenses(List<MemberExpenseRequest> requests,
		List<MemberExpense> memberExpenses) {
		// 1. 요청된 멤버들의 memberId를 추출
		Set<Long> requestMemberIds = requests.stream()
			.map(MemberExpenseRequest::id)
			.collect(Collectors.toSet());

		//2. 멤버별 지출내역중 요청된 멤버 id에 포함되지 않는 지출내역을 찾는다.
		List<MemberExpense> deleteMemberExpenses = memberExpenses.stream()
			.filter(me -> !requestMemberIds.contains(me.getAppointmentMember().getId()))
			.toList();

		memberExpenseDeleter.deleteByMemberExpenses(deleteMemberExpenses);
	}

	public void deleteAllByExpenseId(Long expenseId) {
		memberExpenseDeleter.deleteAllByExpenseId(expenseId);
	}
}
