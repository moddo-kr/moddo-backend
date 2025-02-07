package com.dnd.moddo.domain.expense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseDeleterTest {
	@Mock
	private ExpenseRepository expenseRepository;
	@InjectMocks
	private ExpenseDeleter expenseDeleter;

	@DisplayName("지출내역이 존재하면 해당 지출내역을 삭제에 성공한다.")
	@Test
	void deleteSuccess() {
		//given
		Long expenseId = 1L;
		Expense mockExpense = mock(Expense.class);

		when(expenseRepository.getById(eq(expenseId))).thenReturn(mockExpense);
		doNothing().when(expenseRepository).delete(eq(mockExpense));

		//when
		expenseDeleter.delete(expenseId);

		//then
		verify(expenseRepository, times(1)).getById(eq(expenseId));
		verify(expenseRepository, times(1)).delete(eq(mockExpense));
	}

	@DisplayName("지출내역이 존재하지 않으면 해당 지출내역을 삭제시 예외가 발생한다.")
	@Test
	void deleteNotFoundExpense() {
		//given
		Long expenseId = 1L;
		Expense mockExpense = mock(Expense.class);

		doThrow(new ExpenseNotFoundException(expenseId)).when(expenseRepository).getById(eq(expenseId));

		//when & then
		assertThatThrownBy(() -> {
			expenseDeleter.delete(expenseId);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");
	}
}