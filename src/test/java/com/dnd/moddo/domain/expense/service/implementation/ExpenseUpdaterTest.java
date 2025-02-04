package com.dnd.moddo.domain.expense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseUpdaterTest {
	@Mock
	private ExpenseRepository expenseRepository;
	@InjectMocks
	private ExpenseUpdater expenseUpdater;

	@DisplayName("지출내역이 존재하면 해당 지출내역의 값을 수정할 수 있다.")
	@Test
	void updateSuccess() {
		//given
		Long expenseId = 1L;
		Expense oldExpense = mock(Expense.class);

		when(expenseRepository.getById(eq(expenseId))).thenReturn(oldExpense);

		ExpenseRequest request = mock(ExpenseRequest.class);
		when(request.amount()).thenReturn(10000L);
		when(request.content()).thenReturn("new content");
		when(request.date()).thenReturn(LocalDate.of(2025, 02, 02));

		//when
		Expense newExpense = expenseUpdater.update(expenseId, request);

		//then
		verify(oldExpense, times(1)).update(any(), any(), any());
	}

	@DisplayName("지출내역이 존재하지 않으면 해당 지출내역을 수정하려할 때 예외가 발생한다.")
	@Test
	void updateNotFoundExpense() {
		//given
		Long expenseId = 1L;
		ExpenseRequest request = mock(ExpenseRequest.class);
		when(expenseRepository.getById(eq(expenseId))).thenThrow(new ExpenseNotFoundException(expenseId));

		//when & then
		assertThatThrownBy(() -> {
			expenseUpdater.update(expenseId, request);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");
	}
}