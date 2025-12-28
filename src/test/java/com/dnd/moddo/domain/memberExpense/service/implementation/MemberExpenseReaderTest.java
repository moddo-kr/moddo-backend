package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberExpenseReader;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.MemberExpenseRepository;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class MemberExpenseReaderTest {
	@Mock
	private MemberExpenseRepository memberExpenseRepository;
	@InjectMocks
	private MemberExpenseReader memberExpenseReader;

	private Settlement mockSettlement;
	private Member mockMember;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();

		mockMember = Member.builder()
			.name("박완숙")
			.settlement(mockSettlement)
			.role(ExpenseRole.MANAGER)
			.isPaid(true)
			.build();
	}

	@DisplayName("지출내역이 존재하면 해당 지출내역의 참여자별 지출내역을 조회에 성공한다.")
	@Test
	void findAllByExpenseId_Success() {
		// given
		Long expenseId = 1L;
		// Mock 데이터 준비
		List<MemberExpense> expectedMemberExpense = List.of(
			new MemberExpense(expenseId, mockMember, 15000L)
		);

		when(memberExpenseRepository.findByExpenseId(eq(expenseId))).thenReturn(expectedMemberExpense);

		// when
		List<MemberExpense> result = memberExpenseReader.findAllByExpenseId(expenseId);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result.get(0).getAmount()).isEqualTo(15000L);
		assertThat(result.get(0).getMember()).isEqualTo(mockMember);

		verify(memberExpenseRepository, times(1)).findByExpenseId(eq(expenseId));
	}

	@DisplayName("참여자별 지출내역을 참여자 id, 지출내역 map으로 변환하여 조회할 수 있다.")
	@Test
	void findAllByAppointMemberIds_Success() {
		// given
		Member member1 = Member.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		Member member2 = Member.builder()
			.name("박완숙")
			.settlement(mockSettlement)
			.role(ExpenseRole.MANAGER)
			.build();

		List<MemberExpense> mockExpenses = List.of(
			new MemberExpense(1L, member1, 1000L),
			new MemberExpense(2L, member1, 2000L),
			new MemberExpense(1L, member2, 3000L)
		);

		List<Long> groupMemberIds = List.of(1L, 2L);

		when(memberExpenseRepository.findAllByAppointmentMemberIds(groupMemberIds)).thenReturn(mockExpenses);

		// when
		List<MemberExpense> result = memberExpenseReader.findAllByAppointMemberIds(groupMemberIds);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(mockExpenses.size());

		verify(memberExpenseRepository, times(1)).findAllByAppointmentMemberIds(groupMemberIds);
	}

	@DisplayName("지출내역 id들로 모든 참여자별 지출내역을 조회할 수 있다.")
	@Test
	void findAllByExpenseIds_Success() {
		// given
		Member member1 = Member.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.role(ExpenseRole.PARTICIPANT)
			.build();
		Member member2 = Member.builder()
			.name("박완숙")
			.settlement(mockSettlement)
			.role(ExpenseRole.MANAGER)
			.build();

		List<MemberExpense> mockExpenses = List.of(
			new MemberExpense(1L, member1, 1000L),
			new MemberExpense(2L, member1, 2000L),
			new MemberExpense(1L, member2, 3000L)
		);

		List<Long> expenseIds = List.of(1L, 2L);

		when(memberExpenseRepository.findAllByExpenseIds(eq(expenseIds))).thenReturn(mockExpenses);

		// when
		List<MemberExpense> result = memberExpenseReader.findAllByExpenseIds(expenseIds);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result).hasSize(mockExpenses.size());

		verify(memberExpenseRepository, times(1)).findAllByExpenseIds(eq(expenseIds));
	}
}
