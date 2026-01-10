package com.dnd.moddo.event.application.command;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.MemberExpenseCreator;
import com.dnd.moddo.event.application.impl.MemberExpenseDeleter;
import com.dnd.moddo.event.application.impl.MemberExpenseReader;
import com.dnd.moddo.event.application.impl.MemberExpenseUpdater;
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.presentation.request.MemberExpenseRequest;
import com.dnd.moddo.event.presentation.response.MemberExpenseResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandMemberExpenseService {
	private final MemberReader memberReader;
	private final MemberExpenseCreator memberExpenseCreator;
	private final MemberExpenseReader memberExpenseReader;
	private final MemberExpenseUpdater memberExpenseUpdater;
	private final MemberExpenseDeleter memberExpenseDeleter;

	public List<MemberExpenseResponse> create(Long expenseId, List<MemberExpenseRequest> request) {
		return request.stream()
			.map(m -> {
				Member member = memberReader.findByAppointmentMemberId(m.id());
				return MemberExpenseResponse.of(memberExpenseCreator.create(expenseId, member, m));
			}).toList();

	}

	public List<MemberExpenseResponse> update(Long expenseId, List<MemberExpenseRequest> requests) {
		//1. 지출내역에 딸린 멤버별 지출내역을 모두 가져온다
		List<MemberExpense> memberExpenses = memberExpenseReader.findAllByExpenseId(expenseId);

		Map<Long, MemberExpense> existingMemberExpenses = memberExpenses.stream()
			.collect(Collectors.toMap(me -> me.getMember().getId(), me -> me));

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

		Long appointmentMemberId = request.id();
		MemberExpenseResponse response;

		if (existingMemberExpenses.containsKey(appointmentMemberId)) {
			// 1. 기존 멤버가 있으면 업데이트
			MemberExpense existingMemberExpense = existingMemberExpenses.get(appointmentMemberId);
			memberExpenseUpdater.update(existingMemberExpense, request);
			response = MemberExpenseResponse.of(existingMemberExpense);
		} else {
			// 2. 없는 멤버면 추가
			Member member = memberReader.findByAppointmentMemberId(
				appointmentMemberId);
			MemberExpense newMemberExpense = memberExpenseCreator.create(expenseId, member, request);
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
			.filter(me -> !requestMemberIds.contains(me.getMember().getId()))
			.toList();

		memberExpenseDeleter.deleteByMemberExpenses(deleteMemberExpenses);
	}

	public void deleteAllByExpenseId(Long expenseId) {
		memberExpenseDeleter.deleteAllByExpenseId(expenseId);
	}
}
