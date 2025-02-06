package com.dnd.moddo.domain.expense.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;

@ExtendWith(MockitoExtension.class)
class QueryExpenseServiceTest {

	@Mock
	private ExpenseReader expenseReader;
	@InjectMocks
	private QueryExpenseService queryExpenseService;

	@DisplayName("모임이 존재하면 모임의 모든 지출내역을 조회할 수 있다.")
	@Test
	void findAllByGroupId() {
		//given
		Long groupId = 1L;
		List<Expense> mockExpenses = List.of(
			new Expense(groupId, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03)),
			new Expense(groupId, 35000L, "보드게임카페", LocalDate.of(2025, 02, 03))
		);

		when(expenseReader.findAllByGroupId(eq(groupId))).thenReturn(mockExpenses);
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
		Long groupId = 1L, expenseId = 1L;
		Expense mockExpense = new Expense(groupId, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));

		when(expenseReader.findOneByExpenseId(eq(expenseId))).thenReturn(mockExpense);

		//when
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(expenseId);

		//then
		assertThat(response).isNotNull();
		assertThat(response.amount()).isEqualTo(20000L);
		assertThat(response.content()).isEqualTo("투썸플레이스");

		verify(expenseReader, times(1)).findOneByExpenseId(eq(expenseId));

	}

	@DisplayName("지출내역이 존재하지 않으면 해당 지출내역을 조회할 수 있다.")
	@Test
	void findOneByExpenseIdNotFound() {
		//given
		Long expenseId = 1L;

		when(expenseReader.findOneByExpenseId(eq(expenseId))).thenThrow(new ExpenseNotFoundException(expenseId));

		//when & then
		assertThatThrownBy(() -> {
			queryExpenseService.findOneByExpenseId(expenseId);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");
	}
}