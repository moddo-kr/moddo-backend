package com.dnd.moddo.domain.memberExpense.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.command.CommandMemberExpenseService;
import com.dnd.moddo.event.application.impl.MemberExpenseCreator;
import com.dnd.moddo.event.application.impl.MemberExpenseDeleter;
import com.dnd.moddo.event.application.impl.MemberExpenseReader;
import com.dnd.moddo.event.application.impl.MemberExpenseUpdater;
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.MemberExpenseRequest;
import com.dnd.moddo.event.presentation.response.MemberExpenseResponse;

@ExtendWith(MockitoExtension.class)
class CommandMemberExpenseServiceTest {
	@Mock
	private MemberReader memberReader;
	@Mock
	private MemberExpenseCreator memberExpenseCreator;
	@Mock
	private MemberExpenseReader memberExpenseReader;
	@Mock
	private MemberExpenseUpdater memberExpenseUpdater;
	@Mock
	private MemberExpenseDeleter memberExpenseDeleter;
	@InjectMocks
	private CommandMemberExpenseService commandMemberExpenseService;

	private Settlement mockSettlement = mock(Settlement.class);

	@DisplayName("참여자별 지출내역으르 생성할 때 모든 정보가 유효할 때 참여자별 지출내역 생성에 성공한다.")
	@Test
	void create_Success_MemberExpenses() {
		//given
		Long expenseId = 1L;
		MemberExpenseRequest request1 = new MemberExpenseRequest(1L, 5000L);
		MemberExpenseRequest request2 = new MemberExpenseRequest(2L, 10000L);
		List<MemberExpenseRequest> requests = List.of(request1, request2);

		Member member1 = mock(Member.class);
		Member member2 = mock(Member.class);

		when(member1.getId()).thenReturn(1L);
		when(member2.getId()).thenReturn(2L);

		when(memberReader.findByAppointmentMemberId(any()))
			.thenReturn(member1)
			.thenReturn(member2);

		MemberExpense memberExpense1 = mock(MemberExpense.class);
		MemberExpense memberExpense2 = mock(MemberExpense.class);

		when(memberExpense1.getMember()).thenReturn(member1);
		when(memberExpense2.getMember()).thenReturn(member2);

		when(memberExpenseCreator.create(eq(expenseId), eq(member1), eq(request1)))
			.thenReturn(memberExpense1);
		when(memberExpenseCreator.create(eq(expenseId), eq(member2), eq(request2)))
			.thenReturn(memberExpense2);

		//when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.create(expenseId, requests);

		//then
		assertThat(responses).isNotEmpty();
		assertThat(responses).hasSize(2);

		verify(memberExpenseCreator, times(2)).create(eq(expenseId), any(Member.class),
			any(MemberExpenseRequest.class));
	}

	@DisplayName("지출내역이 수정될때 모든 참여자별 지출내역이 존재했던 정보일 때 참여자별 지출내역 수정에 성공한다.")
	@Test
	void update_Success_MemberExpenses() {
		//given
		Long expenseId = 1L;
		MemberExpenseRequest request1 = new MemberExpenseRequest(1L, 20000L);
		MemberExpenseRequest request2 = new MemberExpenseRequest(2L, 30000L);
		List<MemberExpenseRequest> requests = List.of(request1, request2);

		Member member1 = mock(Member.class);
		Member member2 = mock(Member.class);

		when(member1.getId()).thenReturn(1L);
		when(member2.getId()).thenReturn(2L);

		MemberExpense existingMemberExpense1 = mock(MemberExpense.class);
		MemberExpense existingMemberExpense2 = mock(MemberExpense.class);

		when(existingMemberExpense1.getMember()).thenReturn(member1);
		when(existingMemberExpense2.getMember()).thenReturn(member2);

		List<MemberExpense> exisitingMemberExpenses = List.of(existingMemberExpense1, existingMemberExpense2);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(
			exisitingMemberExpenses);

		doNothing().when(memberExpenseUpdater).update(existingMemberExpense1, request1);
		doNothing().when(memberExpenseUpdater).update(existingMemberExpense2, request2);

		//when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.update(expenseId, requests);

		//then
		assertThat(responses).isNotEmpty();
		assertThat(responses).hasSize(exisitingMemberExpenses.size());

		verify(memberExpenseUpdater, times(2)).update(any(MemberExpense.class), any(MemberExpenseRequest.class));
	}

	@DisplayName("지출내역이 수정될때 일부 참여자의 지출내역이 추가되었을때 참여자별 지출내역 추가 및 수정에 성공한다.")
	@Test
	void addAndUpdate_Success_eMemberExpenses() {
		//given
		Long expenseId = 1L;
		MemberExpenseRequest request1 = new MemberExpenseRequest(1L, 20000L);
		MemberExpenseRequest request2 = new MemberExpenseRequest(2L, 15000L);
		MemberExpenseRequest request3 = new MemberExpenseRequest(3L, 30000L);
		List<MemberExpenseRequest> requests = List.of(request1, request2, request3);

		Member member1 = mock(Member.class);
		Member member2 = mock(Member.class);
		Member expectedMember = mock(Member.class);

		when(member1.getId()).thenReturn(1L);
		when(member2.getId()).thenReturn(2L);
		when(expectedMember.getId()).thenReturn(3L);

		MemberExpense existingMemberExpense1 = mock(MemberExpense.class);
		MemberExpense existingMemberExpense2 = mock(MemberExpense.class);

		when(existingMemberExpense1.getMember()).thenReturn(member1);
		when(existingMemberExpense2.getMember()).thenReturn(member2);

		MemberExpense expectedMemberExpense = new MemberExpense(expenseId, expectedMember, 30000L);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(
			List.of(existingMemberExpense1, existingMemberExpense2));
		when(memberReader.findByAppointmentMemberId(3L)).thenReturn(expectedMember);

		doNothing().when(memberExpenseUpdater).update(existingMemberExpense1, request1);
		doNothing().when(memberExpenseUpdater).update(existingMemberExpense2, request2);
		when(memberExpenseCreator.create(eq(expenseId), any(Member.class), eq(request3))).thenReturn(
			expectedMemberExpense);

		// when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.update(expenseId, requests);

		//then
		assertThat(responses).isNotEmpty();
		assertThat(responses).hasSize(3);

		verify(memberExpenseUpdater, times(2)).update(any(MemberExpense.class), any(MemberExpenseRequest.class));
		verify(memberExpenseCreator, times(1)).create(eq(expenseId), any(Member.class),
			any(MemberExpenseRequest.class));
	}

	@DisplayName("원래 참여자별 지출내역에 존재하지만 수정 요청에 참여자id와 금액이 포함되어있지 않다면 참여자 지출내역에서 삭제하고 이외의 요청에 대해서 수정에 성공한다.")
	@Test
	void deleteAndUpdate_Success_MemberExpenses() {
		//given
		Long expenseId = 1L;
		MemberExpenseRequest request1 = new MemberExpenseRequest(1L, 20000L);
		List<MemberExpenseRequest> requests = List.of(request1);

		Member member1 = mock(Member.class);
		Member member2 = mock(Member.class);

		when(member1.getId()).thenReturn(1L);
		when(member2.getId()).thenReturn(2L);

		MemberExpense existingMemberExpense1 = mock(MemberExpense.class);
		MemberExpense existingMemberExpense2 = mock(MemberExpense.class);

		when(existingMemberExpense1.getMember()).thenReturn(member1);
		when(existingMemberExpense2.getMember()).thenReturn(member2);
		when(existingMemberExpense1.getAmount()).thenReturn(5000L);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(
			List.of(existingMemberExpense1, existingMemberExpense2));

		doNothing().when(memberExpenseUpdater).update(existingMemberExpense1, request1);
		doNothing().when(memberExpenseDeleter).deleteByMemberExpenses(any());

		//when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.update(expenseId, requests);

		//then
		assertThat(responses).isNotEmpty();
		assertThat(responses).hasSize(1);

		verify(memberExpenseUpdater, times(1)).update(any(MemberExpense.class), any(MemberExpenseRequest.class));
		verify(memberExpenseDeleter, times(1)).deleteByMemberExpenses(any());
	}
}