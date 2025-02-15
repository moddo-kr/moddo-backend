package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;

@ExtendWith(MockitoExtension.class)
class MemberExpenseReaderTest {
	@Mock
	private MemberExpenseRepository memberExpenseRepository;
	@InjectMocks
	private MemberExpenseReader memberExpenseReader;

	private Group mockGroup;
	private GroupMember mockGroupMember;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
		mockGroupMember = new GroupMember("박완숙", mockGroup, ExpenseRole.MANAGER);
	}

	@DisplayName("지출내역이 존재하면 해당 지출내역의 참여자별 지출내역을 조회에 성공한다.")
	@Test
	void findAllByExpenseIdS() {
		//given
		Long expenseId = 1L;
		List<MemberExpense> expectedMemberExpense = List.of(new MemberExpense(expenseId, mockGroupMember, 15000L));

		when(memberExpenseRepository.findByExpenseId(eq(expenseId))).thenReturn(expectedMemberExpense);

		//when
		List<MemberExpense> result = memberExpenseReader.findAllByExpenseId(expenseId);

		//then
		assertThat(result).isNotEmpty();
		assertThat(result.get(0).getAmount()).isEqualTo(15000L);
		assertThat(result.get(0).getGroupMember()).isEqualTo(mockGroupMember);

		verify(memberExpenseRepository, times(1)).findByExpenseId(eq(expenseId));
	}

	@DisplayName("참여자별 지출내역을 참여자id, 지출내역 map으로 변환하여 조회할 수 있다.")
	@Test
	public void findAllByGroupMemberIds_Success() {
		//given
		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);

		List<MemberExpense> mockExpenses = List.of(
			new MemberExpense(1L, groupMember1, 1000L),
			new MemberExpense(2L, groupMember1, 2000L),
			new MemberExpense(1L, groupMember2, 3000L)
		);

		List<Long> groupMemberIds = List.of(1L, 2L);

		when(memberExpenseRepository.findAllByGroupMemberIds(groupMemberIds)).thenReturn(mockExpenses);

		//when
		List<MemberExpense> result = memberExpenseReader.findAllByGroupMemberIds(groupMemberIds);

		//then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(mockExpenses.size());

		verify(memberExpenseRepository, times(1)).findAllByGroupMemberIds(groupMemberIds);
	}

	@DisplayName("지출내역 id들로 모든 참여자별 지출내역을 조회할 수 있다.")
	@Test
	void findAllByExpenseIds_Success() {
		//given
		GroupMember groupMember1 = mock(GroupMember.class);
		GroupMember groupMember2 = mock(GroupMember.class);

		List<MemberExpense> mockExpenses = List.of(
			new MemberExpense(1L, groupMember1, 1000L),
			new MemberExpense(2L, groupMember1, 2000L),
			new MemberExpense(1L, groupMember2, 3000L)
		);

		List<Long> expenseIds = List.of(1L, 2L);

		when(memberExpenseRepository.findAllByExpenseIds(eq(expenseIds))).thenReturn(mockExpenses);

		//then
		List<MemberExpense> result = memberExpenseReader.findAllByExpenseIds(expenseIds);

		//then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(mockExpenses.size());
	}
}