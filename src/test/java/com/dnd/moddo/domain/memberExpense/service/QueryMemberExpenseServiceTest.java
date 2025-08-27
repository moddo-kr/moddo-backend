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

import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMembersExpenseResponse;
import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberReader;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class QueryMemberExpenseServiceTest {
	@Mock
	private MemberExpenseReader memberExpenseReader;
	@Mock
	private ExpenseReader expenseReader;
	@Mock
	private AppointmentMemberReader appointmentMemberReader;
	@InjectMocks
	private QueryMemberExpenseService queryMemberExpenseService;

	private Settlement mockSettlement;
	private AppointmentMember mockAppointmentMember1;
	private AppointmentMember mockAppointmentMember2;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();

		mockAppointmentMember1 = AppointmentMember.builder()
			.name("김모또")
			.settlement(mockSettlement)
			.isPaid(true)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		mockAppointmentMember2 = AppointmentMember.builder()
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
			new MemberExpense(expenseId, mockAppointmentMember1, 15000L),
			new MemberExpense(expenseId, mockAppointmentMember2, 5000L)
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
		AppointmentMember appointmentMember1 = mock(AppointmentMember.class);
		AppointmentMember appointmentMember2 = mock(AppointmentMember.class);

		when(appointmentMember1.getId()).thenReturn(1L);
		when(appointmentMember2.getId()).thenReturn(2L);

		List<AppointmentMember> appointmentMembers = List.of(appointmentMember1, appointmentMember2);

		MemberExpense memberExpense1 = mock(MemberExpense.class);
		MemberExpense memberExpense2 = mock(MemberExpense.class);
		when(memberExpense1.getExpenseId()).thenReturn(1L);
		when(memberExpense1.getAmount()).thenReturn(10000L);
		when(memberExpense1.getAppointmentMember()).thenReturn(appointmentMember1);

		when(memberExpense2.getExpenseId()).thenReturn(2L);
		when(memberExpense2.getAmount()).thenReturn(15000L);
		when(memberExpense2.getAppointmentMember()).thenReturn(appointmentMember2);

		Expense expense1 = mock(Expense.class);
		Expense expense2 = mock(Expense.class);
		when(expense1.getId()).thenReturn(1L);
		when(expense2.getId()).thenReturn(2L);

		when(appointmentMemberReader.findAllByGroupId(eq(groupId))).thenReturn(appointmentMembers);
		when(memberExpenseReader.findAllByGroupMemberIds(List.of(1L, 2L)))
			.thenReturn(List.of(memberExpense1, memberExpense2));
		when(expenseReader.findAllByGroupId(any())).thenReturn(List.of(expense1, expense2));

		// when
		AppointmentMembersExpenseResponse response = queryMemberExpenseService.findMemberExpenseDetailsByGroupId(
			groupId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.memberExpenses()).hasSize(2);

		verify(appointmentMemberReader, times(1)).findAllByGroupId(groupId);
		verify(memberExpenseReader, times(1)).findAllByGroupMemberIds(anyList());
		verify(expenseReader, times(1)).findAllByGroupId(groupId);
	}

	@DisplayName("지출내역 id를 통해 참여자 지출내역을 찾아 지출내역 id에 해당하는 참여자 이름들을 map으로 조회할 수 있다.")
	@Test
	void getMemberNamesByExpenseIds_Success() {
		//given
		AppointmentMember appointmentMember1 = mock(AppointmentMember.class);
		AppointmentMember appointmentMember2 = mock(AppointmentMember.class);

		when(appointmentMember1.getName()).thenReturn("김모또");
		when(appointmentMember2.getName()).thenReturn("김반숙");

		when(appointmentMember1.isManager()).thenReturn(true);
		when(appointmentMember2.isManager()).thenReturn(false);

		List<Long> expenseIds = List.of(1L, 2L);

		List<MemberExpense> mockExpenses = List.of(
			new MemberExpense(1L, appointmentMember1, 1000L),
			new MemberExpense(2L, appointmentMember1, 2000L),
			new MemberExpense(1L, appointmentMember2, 3000L)
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
