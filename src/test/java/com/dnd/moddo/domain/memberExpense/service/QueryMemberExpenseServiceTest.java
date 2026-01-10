package com.dnd.moddo.domain.memberExpense.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.ExpenseReader;
import com.dnd.moddo.event.application.impl.MemberExpenseReader;
import com.dnd.moddo.event.application.impl.MemberReader;
import com.dnd.moddo.event.application.query.QueryMemberExpenseService;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.response.MemberExpenseResponse;
import com.dnd.moddo.event.presentation.response.MembersExpenseResponse;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class QueryMemberExpenseServiceTest {
	@Mock
	private MemberExpenseReader memberExpenseReader;
	@Mock
	private ExpenseReader expenseReader;
	@Mock
	private MemberReader memberReader;
	@InjectMocks
	private QueryMemberExpenseService queryMemberExpenseService;

	private Settlement mockSettlement;
	private Member mockMember1;
	private Member mockMember2;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();

		mockMember1 = Member.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.isPaid(true)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		mockMember2 = Member.builder()
			.name("박완숙")
			.settlement(mockSettlement)
			.isPaid(false)
			.profileId(1)
			.role(ExpenseRole.PARTICIPANT)
			.build();
	}

	@DisplayName("지출내역이 유효할때 지출내역의 참여자별 지출내역 조회에 성공한다.")
	@Test
	void findAllByExpenseId() {
		//given
		Long expenseId = 1L;

		List<MemberExpense> expectedMemberExpense = List.of(
			new MemberExpense(expenseId, mockMember1, 15000L),
			new MemberExpense(expenseId, mockMember2, 5000L)
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
	void findMemberExpenseDetailsBySettlementId_Success() {
		//given
		Long groupId = 1L;
		Member member1 = mock(Member.class);
		Member member2 = mock(Member.class);

		when(member1.getId()).thenReturn(1L);
		when(member2.getId()).thenReturn(2L);

		List<Member> members = List.of(member1, member2);

		MemberExpense memberExpense1 = mock(MemberExpense.class);
		MemberExpense memberExpense2 = mock(MemberExpense.class);
		when(memberExpense1.getExpenseId()).thenReturn(1L);
		when(memberExpense1.getAmount()).thenReturn(10000L);
		when(memberExpense1.getMember()).thenReturn(member1);

		when(memberExpense2.getExpenseId()).thenReturn(2L);
		when(memberExpense2.getAmount()).thenReturn(15000L);
		when(memberExpense2.getMember()).thenReturn(member2);

		Expense expense1 = mock(Expense.class);
		Expense expense2 = mock(Expense.class);
		when(expense1.getId()).thenReturn(1L);
		when(expense2.getId()).thenReturn(2L);

		when(memberReader.findAllBySettlementId(eq(groupId))).thenReturn(members);
		when(memberExpenseReader.findAllByAppointMemberIds(List.of(1L, 2L)))
			.thenReturn(List.of(memberExpense1, memberExpense2));
		when(expenseReader.findAllBySettlementId(any())).thenReturn(List.of(expense1, expense2));

		// when
		MembersExpenseResponse response = queryMemberExpenseService.findMemberExpenseDetailsBySettlementId(
			groupId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.memberExpenses()).hasSize(2);

		verify(memberReader, times(1)).findAllBySettlementId(groupId);
		verify(memberExpenseReader, times(1)).findAllByAppointMemberIds(anyList());
		verify(expenseReader, times(1)).findAllBySettlementId(groupId);
	}

	@DisplayName("지출내역 id를 통해 참여자 지출내역을 찾아 지출내역 id에 해당하는 참여자 이름들을 map으로 조회할 수 있다.")
	@Test
	void getMemberNamesByExpenseIds_Success() {
		//given
		Member member1 = mock(Member.class);
		Member member2 = mock(Member.class);

		when(member1.getName()).thenReturn("김모또");
		when(member2.getName()).thenReturn("김반숙");

		when(member1.isManager()).thenReturn(true);
		when(member2.isManager()).thenReturn(false);

		List<Long> expenseIds = List.of(1L, 2L);

		List<MemberExpense> mockExpenses = List.of(
			new MemberExpense(1L, member1, 1000L),
			new MemberExpense(2L, member1, 2000L),
			new MemberExpense(1L, member2, 3000L)
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
