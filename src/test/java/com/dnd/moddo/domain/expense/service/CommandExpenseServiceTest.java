package com.dnd.moddo.domain.expense.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseCreator;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseDeleter;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseUpdater;

@ExtendWith(MockitoExtension.class)
class CommandExpenseServiceTest {

	@Mock
	private ExpenseCreator expenseCreator;
	@Mock
	private ExpenseUpdater expenseUpdater;
	@Mock
	private ExpenseDeleter expenseDeleter;
	@InjectMocks
	private CommandExpenseService commandExpenseService;

	@DisplayName("모임이 존재할 때 여러 지출 내역 생성에 성공한다.")
	@Test
	void createExpense() {
		//given
		Long meetId = 1L;
		ExpensesRequest request = new ExpensesRequest(new ArrayList<>());
		List<Expense> mockExpenses = List.of(new Expense(meetId, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03)),
			new Expense(meetId, 100000L, "하이디라오", LocalDate.of(2025, 02, 03)));

		when(expenseCreator.create(eq(meetId), eq(request))).thenReturn(mockExpenses);

		//when
		ExpensesResponse response = commandExpenseService.createExpense(meetId, request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.expenses().size()).isEqualTo(mockExpenses.size());
		assertThat(response.expenses().get(0).amount()).isEqualTo(20000L);
		assertThat(response.expenses().get(0).date()).isEqualTo("2025-02-03");
		assertThat(response.expenses().get(0).content()).isEqualTo("투썸플레이스");
		verify(expenseCreator, times(1)).create(eq(meetId), eq(request));
	}

	@DisplayName("지출내역이 존재할 때 기존의 지출내역을 수정할 수 있다.")
	@Test
	void updateExpenseSuccess() {
		//given
		Long meetId = 1L, expenseId = 1L;
		Expense mockExpense = new Expense(meetId, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));
		ExpenseRequest expenseRequest = mock(ExpenseRequest.class);
		ExpenseResponse expectedResponse = ExpenseResponse.of(mockExpense);

		when(expenseUpdater.update(eq(expenseId), eq(expenseRequest))).thenReturn(mockExpense);
		// when
		ExpenseResponse response = commandExpenseService.updateExpense(expenseId, expenseRequest);

		//then
		assertThat(response).isNotNull();
		assertThat(response).isEqualTo(expectedResponse);

		verify(expenseUpdater, times(1)).update(expenseId, expenseRequest);
	}

	@DisplayName("업데이트하려는 지출 내역을 찾을 수 없을때 예외를 발생시킨다.")
	@Test
	void updateExpenseNotFound() {
		//given
		Long expenseId = 1L;
		ExpenseRequest expenseRequest = mock(ExpenseRequest.class);

		when(expenseUpdater.update(eq(expenseId), eq(expenseRequest))).thenThrow(
			new ExpenseNotFoundException(expenseId));

		assertThatThrownBy(() -> {
			expenseUpdater.update(expenseId, expenseRequest);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");

	}

	@DisplayName("삭제하려는 지출내역이 존재하면 지출내역을 삭제에 성공한다.")
	@Test
	void deleteExpenseSuccess() {
		//given
		Long expenseId = 1L;
		doNothing().when(expenseDeleter).delete(eq(expenseId));
		//when
		commandExpenseService.deleteExpense(expenseId);
		//then
		verify(expenseDeleter, times(1)).delete(eq(expenseId));
	}

	@DisplayName("삭제하려는 지출내역이 존재하지 않는다면 예외가 발생한다.")
	@Test
	void deleteExpenseNotFound() {
		//given
		Long expenseId = 1L;
		doThrow(new ExpenseNotFoundException(expenseId)).when(expenseDeleter).delete(eq(expenseId));
		//when & then
		assertThatThrownBy(() -> {
			commandExpenseService.deleteExpense(expenseId);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");

	}
}