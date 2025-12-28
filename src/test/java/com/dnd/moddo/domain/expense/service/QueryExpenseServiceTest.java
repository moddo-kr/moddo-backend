package com.dnd.moddo.domain.expense.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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
import com.dnd.moddo.event.application.query.QueryExpenseService;
import com.dnd.moddo.event.application.query.QueryMemberExpenseService;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.response.ExpenseDetailsResponse;
import com.dnd.moddo.event.presentation.response.ExpenseResponse;
import com.dnd.moddo.event.presentation.response.ExpensesResponse;
import com.dnd.moddo.event.presentation.response.MemberExpenseResponse;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class QueryExpenseServiceTest {

	@Mock
	private ExpenseReader expenseReader;
	@Mock
	private QueryMemberExpenseService queryMemberExpenseService;
	@InjectMocks
	private QueryExpenseService queryExpenseService;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("모임이 존재하면 모임의 모든 지출내역을 조회할 수 있다.")
	@Test
	void findAllBySettlementId() {
		//given
		Long groupId = mockSettlement.getId();
		List<Expense> mockExpenses = List.of(
			new Expense(mockSettlement, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03)),
			new Expense(mockSettlement, 35000L, "보드게임카페", LocalDate.of(2025, 02, 03))
		);

		when(expenseReader.findAllBySettlementId(eq(groupId))).thenReturn(mockExpenses);
		Long expenseId1 = 1L, expenseId2 = 2L;

		List<MemberExpenseResponse> responses1 = List.of(
			new MemberExpenseResponse(1L, ExpenseRole.MANAGER, "김모또", null, 15000L),
			new MemberExpenseResponse(2L, ExpenseRole.PARTICIPANT, "박완숙", null, 5000L));
		List<MemberExpenseResponse> responses2 = List.of(
			new MemberExpenseResponse(1L, ExpenseRole.MANAGER, "김모또", null, 15000L),
			new MemberExpenseResponse(2L, ExpenseRole.PARTICIPANT, "박완숙", null, 2000L));

		when(queryMemberExpenseService.findAllByExpenseId(any()))
			.thenReturn(responses1)
			.thenReturn(responses2);

		//when
		ExpensesResponse response = queryExpenseService.findAllBySettlementId(groupId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.expenses().size()).isEqualTo(mockExpenses.size());
		assertThat(response.expenses().get(0).content()).isEqualTo("투썸플레이스");

		verify(expenseReader, times(1)).findAllBySettlementId(eq(groupId));

	}

	@DisplayName("지출내역이 존재하면 해당 지출내역을 조회할 수 있다.")
	@Test
	void findOneByExpenseIdSuccess() {
		//given
		Long groupId = mockSettlement.getId(), expenseId = 1L;
		Expense mockExpense = new Expense(mockSettlement, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));

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
	void findAllExpenseDetailsBySettlementId_Success() {
		//given
		Long groupId = 1L;
		Expense expense1 = mock(Expense.class);
		Expense expense2 = mock(Expense.class);
		Expense expense3 = mock(Expense.class);

		when(expense1.getId()).thenReturn(1L);
		when(expense2.getId()).thenReturn(2L);
		when(expense3.getId()).thenReturn(3L);

		List<Expense> mockExpense = List.of(expense1, expense2, expense3);

		when(expenseReader.findAllBySettlementId(eq(groupId))).thenReturn(mockExpense);

		when(queryMemberExpenseService.getMemberNamesByExpenseIds(eq(List.of(1L, 2L, 3L)))).thenReturn(Map.of(
			1L, List.of("김모또", "김반숙"),
			2L, List.of("김모또", "김반숙", "정에그"),
			3L, List.of("김모또", "연노른자")
		));

		//when
		ExpenseDetailsResponse response = queryExpenseService.findAllExpenseDetailsBySettlementId(groupId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.expenses()).hasSize(3);
		assertThat(response.expenses().get(0).groupMembers()).hasSize(2);

		verify(expenseReader, times(1)).findAllBySettlementId(eq(groupId));
		verify(queryMemberExpenseService, times(1)).getMemberNamesByExpenseIds(any());
	}
}