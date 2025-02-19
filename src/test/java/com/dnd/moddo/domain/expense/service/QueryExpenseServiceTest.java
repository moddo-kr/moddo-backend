package com.dnd.moddo.domain.expense.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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

import com.dnd.moddo.domain.expense.dto.response.ExpenseDetailsResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.service.QueryMemberExpenseService;

@ExtendWith(MockitoExtension.class)
class QueryExpenseServiceTest {

	@Mock
	private ExpenseReader expenseReader;
	@Mock
	private QueryMemberExpenseService queryMemberExpenseService;
	@InjectMocks
	private QueryExpenseService queryExpenseService;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌", LocalDateTime.now().plusDays(1));
	}

	@DisplayName("모임이 존재하면 모임의 모든 지출내역을 조회할 수 있다.")
	@Test
	void findAllByGroupId() {
		//given
		Long groupId = mockGroup.getId();
		List<Expense> mockExpenses = List.of(
			new Expense(mockGroup, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03)),
			new Expense(mockGroup, 35000L, "보드게임카페", LocalDate.of(2025, 02, 03))
		);

		when(expenseReader.findAllByGroupId(eq(groupId))).thenReturn(mockExpenses);
		Long expenseId1 = 1L, expenseId2 = 2L;

		List<MemberExpenseResponse> responses1 = List.of(
			new MemberExpenseResponse(1L, ExpenseRole.MANAGER, "김모또", 15000L),
			new MemberExpenseResponse(2L, ExpenseRole.PARTICIPANT, "박완숙", 5000L));
		List<MemberExpenseResponse> responses2 = List.of(
			new MemberExpenseResponse(1L, ExpenseRole.MANAGER, "김모또", 15000L),
			new MemberExpenseResponse(2L, ExpenseRole.PARTICIPANT, "박완숙", 2000L));

		when(queryMemberExpenseService.findAllByExpenseId(any()))
			.thenReturn(responses1)
			.thenReturn(responses2);

		//when
		ExpensesResponse response = queryExpenseService.findAllByGroupId(groupId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.expenses().size()).isEqualTo(mockExpenses.size());
		assertThat(response.expenses().get(0).content()).isEqualTo("투썸플레이스");

		verify(expenseReader, times(1)).findAllByGroupId(eq(groupId));

	}

	@DisplayName("지출내역이 존재하면 해당 지출내역을 조회할 수 있다.")
	@Test
	void findOneByExpenseIdSuccess() {
		//given
		Long groupId = mockGroup.getId(), expenseId = 1L;
		Expense mockExpense = new Expense(mockGroup, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));

		when(expenseReader.findByExpenseId(eq(expenseId))).thenReturn(mockExpense);

		//when
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(expenseId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.amount()).isEqualTo(20000L);
		assertThat(response.content()).isEqualTo("투썸플레이스");

		verify(expenseReader, times(1)).findByExpenseId(eq(expenseId));

	}

	@DisplayName("지출내역이 존재하지 않으면 해당 지출내역을 조회할 수 있다.")
	@Test
	void findOneByExpenseIdNotFound() {
		//given
		Long expenseId = 1L;

		when(expenseReader.findByExpenseId(eq(expenseId))).thenThrow(new ExpenseNotFoundException(expenseId));

		//when & then
		assertThatThrownBy(() -> {
			queryExpenseService.findOneByExpenseId(expenseId);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");
	}

	@DisplayName("모임 id가 유효하면 모임에 속한 전체 지출내역을 조회할 수 있다.")
	@Test
	void findAllExpenseDetailsByGroupId_Success() {
		//given
		Long groupId = 1L;
		Expense expense1 = mock(Expense.class);
		Expense expense2 = mock(Expense.class);
		Expense expense3 = mock(Expense.class);

		when(expense1.getId()).thenReturn(1L);
		when(expense2.getId()).thenReturn(2L);
		when(expense3.getId()).thenReturn(3L);

		List<Expense> mockExpense = List.of(expense1, expense2, expense3);

		when(expenseReader.findAllByGroupId(eq(groupId))).thenReturn(mockExpense);

		when(queryMemberExpenseService.getMemberNamesByExpenseIds(eq(List.of(1L, 2L, 3L)))).thenReturn(Map.of(
			1L, List.of("김모또", "김반숙"),
			2L, List.of("김모또", "김반숙", "정에그"),
			3L, List.of("김모또", "연노른자")
		));

		//when
		ExpenseDetailsResponse response = queryExpenseService.findAllExpenseDetailsByGroupId(groupId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.expenses()).hasSize(3);
		assertThat(response.expenses().get(0).groupMembers()).hasSize(2);

		verify(expenseReader, times(1)).findAllByGroupId(eq(groupId));
		verify(queryMemberExpenseService, times(1)).getMemberNamesByExpenseIds(any());
	}
}