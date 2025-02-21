package com.dnd.moddo.domain.memberExpense.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersExpenseResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;

@ExtendWith(MockitoExtension.class)
class QueryMemberExpenseServiceTest {
	@Mock
	private MemberExpenseReader memberExpenseReader;
	@Mock
	private ExpenseReader expenseReader;
	@Mock
	private GroupMemberReader groupMemberReader;
	@InjectMocks
	private QueryMemberExpenseService queryMemberExpenseService;

	private Group mockGroup;
	private GroupMember mockGroupMember1;
	private GroupMember mockGroupMember2;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now().plusMinutes(1),
			"은행", "계좌", LocalDateTime.now().plusDays(1));

		mockGroupMember1 = GroupMember.builder()
			.name("김모또")
			.profile("profile1.jpg")
			.group(mockGroup)
			.isPaid(true)
			.role(ExpenseRole.MANAGER)
			.build();

		mockGroupMember2 = GroupMember.builder()
			.name("박완숙")
			.profile("profile2.jpg")
			.group(mockGroup)
			.isPaid(false)
			.role(ExpenseRole.PARTICIPANT)
			.build();
	}

	@DisplayName("지출내역이 유효할때 지출내역의 참여자별 지출내역 조회에 성공한다.")
	@Test
	void findAllByExpenseId() {
		//given
		Long expenseId = 1L;

		List<MemberExpense> expectedMemberExpense = List.of(
			new MemberExpense(expenseId, mockGroupMember1, 15000L),
			new MemberExpense(expenseId, mockGroupMember2, 5000L)
		);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(expectedMemberExpense);

		//when
		List<MemberExpenseResponse> responses = queryMemberExpenseService.findAllByExpenseId(expenseId);

		//then
		assertThat(responses).isNotEmpty();
		assertThat(responses).hasSize(2);
		assertThat(responses.get(0).name()).isEqualTo("김모또");
		assertThat(responses.get(0).amount()).isEqualTo(15000L);
	}

	@DisplayName("모임이 유효할 때 참여자별 정산내역 조회에 성공한다.")
	@Test
	void findMemberExpenseDetailsByGroupId_Success() {
		//given
		Long groupId = 1L;
		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);

		when(groupMember1.getId()).thenReturn(1L);
		when(groupMember2.getId()).thenReturn(2L);

		List<GroupMember> groupMembers = List.of(groupMember1, groupMember2);

		MemberExpense memberExpense1 = mock(MemberExpense.class);
		MemberExpense memberExpense2 = mock(MemberExpense.class);
		when(memberExpense1.getExpenseId()).thenReturn(1L);
		when(memberExpense1.getAmount()).thenReturn(10000L);
		when(memberExpense1.getGroupMember()).thenReturn(groupMember1);

		when(memberExpense2.getExpenseId()).thenReturn(2L);
		when(memberExpense2.getAmount()).thenReturn(15000L);
		when(memberExpense2.getGroupMember()).thenReturn(groupMember2);

		Expense expense1 = mock(Expense.class);
		Expense expense2 = mock(Expense.class);
		when(expense1.getId()).thenReturn(1L);
		when(expense2.getId()).thenReturn(2L);

		when(groupMemberReader.findAllByGroupId(eq(groupId))).thenReturn(groupMembers);
		when(memberExpenseReader.findAllByGroupMemberIds(List.of(1L, 2L)))
			.thenReturn(List.of(memberExpense1, memberExpense2));
		when(expenseReader.findAllByGroupId(any())).thenReturn(List.of(expense1, expense2));

		// when
		GroupMembersExpenseResponse response = queryMemberExpenseService.findMemberExpenseDetailsByGroupId(groupId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.memberExpenses()).hasSize(2);

		verify(groupMemberReader, times(1)).findAllByGroupId(groupId);
		verify(memberExpenseReader, times(1)).findAllByGroupMemberIds(anyList());
		verify(expenseReader, times(1)).findAllByGroupId(groupId);
	}

	@DisplayName("지출내역 id를 통해 참여자 지출내역을 찾아 지출내역 id에 해당하는 참여자 이름들을 map으로 조회할 수 있다.")
	@Test
	void getMemberNamesByExpenseIds_Success() {
		//given
		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);

		when(groupMember1.getName()).thenReturn("김모또");
		when(groupMember2.getName()).thenReturn("김반숙");

		when(groupMember1.isManager()).thenReturn(true);
		when(groupMember2.isManager()).thenReturn(false);

		List<Long> expenseIds = List.of(1L, 2L);

		List<MemberExpense> mockExpenses = List.of(
			new MemberExpense(1L, groupMember1, 1000L),
			new MemberExpense(2L, groupMember1, 2000L),
			new MemberExpense(1L, groupMember2, 3000L)
		);

		when(memberExpenseReader.findAllByExpenseIds(eq(expenseIds))).thenReturn(mockExpenses);

		//when
		Map<Long, List<String>> result = queryMemberExpenseService.getMemberNamesByExpenseIds(expenseIds);

		//then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(2);
		assertThat(result.get(1L)).hasSize(2);
		assertThat(result.get(1L).get(0)).endsWith("(총무)");
		assertThat(result.get(1L).get(1)).isEqualTo("김반숙");
		assertThat(result.get(2L)).hasSize(1);

		verify(memberExpenseReader, times(1)).findAllByExpenseIds(eq(expenseIds));
	}
}
