package com.dnd.moddo.domain.memberExpense.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseCreator;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseDeleter;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseUpdater;

@ExtendWith(MockitoExtension.class)
class CommandMemberExpenseServiceTest {
	@Mock
	private GroupMemberReader groupMemberReader;
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

	private Group mockGroup = mock(Group.class);

	@DisplayName("참여자별 지출내역으르 생성할 때 모든 정보가 유효할 때 참여자별 지출내역 생성에 성공한다.")
	@Test
	void create_Success_MemberExpenses() {
		//given
		Long expenseId = 1L;
		MemberExpenseRequest request1 = new MemberExpenseRequest(1L, 5000L);
		MemberExpenseRequest request2 = new MemberExpenseRequest(2L, 10000L);
		List<MemberExpenseRequest> requests = List.of(request1, request2);

		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);

		when(groupMember1.getId()).thenReturn(1L);
		when(groupMember2.getId()).thenReturn(2L);

		when(groupMemberReader.findByGroupMemberId(any()))
			.thenReturn(groupMember1)
			.thenReturn(groupMember2);

		MemberExpense memberExpense1 = mock(MemberExpense.class);
		MemberExpense memberExpense2 = mock(MemberExpense.class);

		when(memberExpense1.getGroupMember()).thenReturn(groupMember1);
		when(memberExpense2.getGroupMember()).thenReturn(groupMember2);

		when(memberExpenseCreator.create(eq(expenseId), eq(groupMember1), eq(request1)))
			.thenReturn(memberExpense1);
		when(memberExpenseCreator.create(eq(expenseId), eq(groupMember2), eq(request2)))
			.thenReturn(memberExpense2);

		//when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.create(expenseId, requests);

		//then
		assertThat(!responses.isEmpty()).isTrue();
		assertThat(responses.size()).isEqualTo(2);

		verify(memberExpenseCreator, times(2)).create(eq(expenseId), any(GroupMember.class),
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

		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);

		when(groupMember1.getId()).thenReturn(1L);
		when(groupMember2.getId()).thenReturn(2L);

		MemberExpense existingMemberExpense1 = mock(MemberExpense.class);
		MemberExpense existingMemberExpense2 = mock(MemberExpense.class);

		when(existingMemberExpense1.getGroupMember()).thenReturn(groupMember1);
		when(existingMemberExpense2.getGroupMember()).thenReturn(groupMember2);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(
			List.of(existingMemberExpense1, existingMemberExpense2));

		doNothing().when(memberExpenseUpdater).update(existingMemberExpense1, request1);
		doNothing().when(memberExpenseUpdater).update(existingMemberExpense2, request2);

		//when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.update(expenseId, requests);

		//then
		assertThat(responses.isEmpty()).isFalse();
		assertThat(responses.size()).isEqualTo(2);

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

		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);
		GroupMember expectedGroupMember = mock(GroupMember.class);

		when(groupMember1.getId()).thenReturn(1L);
		when(groupMember2.getId()).thenReturn(2L);
		when(expectedGroupMember.getId()).thenReturn(3L);

		MemberExpense existingMemberExpense1 = mock(MemberExpense.class);
		MemberExpense existingMemberExpense2 = mock(MemberExpense.class);

		when(existingMemberExpense1.getGroupMember()).thenReturn(groupMember1);
		when(existingMemberExpense2.getGroupMember()).thenReturn(groupMember2);

		MemberExpense expectedMemberExpense = new MemberExpense(expenseId, expectedGroupMember, 30000L);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(
			List.of(existingMemberExpense1, existingMemberExpense2));
		when(groupMemberReader.findByGroupMemberId(3L)).thenReturn(expectedGroupMember);

		doNothing().when(memberExpenseUpdater).update(existingMemberExpense1, request1);
		doNothing().when(memberExpenseUpdater).update(existingMemberExpense2, request2);
		when(memberExpenseCreator.create(eq(expenseId), any(GroupMember.class), eq(request3))).thenReturn(
			expectedMemberExpense);

		// when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.update(expenseId, requests);

		//then
		assertThat(responses.isEmpty()).isFalse();
		assertThat(responses.size()).isEqualTo(3);

		verify(memberExpenseUpdater, times(2)).update(any(MemberExpense.class), any(MemberExpenseRequest.class));
		verify(memberExpenseCreator, times(1)).create(eq(expenseId), any(GroupMember.class),
			any(MemberExpenseRequest.class));
	}

	@DisplayName("원래 참여자별 지출내역에 존재하지만 수정 요청에 참여자id와 금액이 포함되어있지 않다면 참여자 지출내역에서 삭제하고 이외의 요청에 대해서 수정에 성공한다.")
	@Test
	void deleteAndUpdate_Success_MemberExpenses() {
		//given
		Long expenseId = 1L;
		MemberExpenseRequest request1 = new MemberExpenseRequest(1L, 20000L);
		List<MemberExpenseRequest> requests = List.of(request1);

		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);

		when(groupMember1.getId()).thenReturn(1L);
		when(groupMember2.getId()).thenReturn(2L);

		MemberExpense existingMemberExpense1 = mock(MemberExpense.class);
		MemberExpense existingMemberExpense2 = mock(MemberExpense.class);

		when(existingMemberExpense1.getGroupMember()).thenReturn(groupMember1);
		when(existingMemberExpense2.getGroupMember()).thenReturn(groupMember2);
		when(existingMemberExpense1.getAmount()).thenReturn(5000L);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(
			List.of(existingMemberExpense1, existingMemberExpense2));

		doNothing().when(memberExpenseUpdater).update(existingMemberExpense1, request1);
		doNothing().when(memberExpenseDeleter).deleteByMemberExpenses(any());

		//when
		List<MemberExpenseResponse> responses = commandMemberExpenseService.update(expenseId, requests);

		//then
		assertThat(!responses.isEmpty()).isTrue();
		assertThat(responses.size()).isEqualTo(1);

		verify(memberExpenseUpdater, times(1)).update(any(MemberExpense.class), any(MemberExpenseRequest.class));
		verify(memberExpenseDeleter, times(1)).deleteByMemberExpenses(any());
	}
}