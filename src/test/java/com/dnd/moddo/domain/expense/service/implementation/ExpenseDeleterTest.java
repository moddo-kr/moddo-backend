package com.dnd.moddo.domain.expense.service.implementation;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.entity.Expense;
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

		doNothing().when(expenseRepository).delete(eq(mockExpense));

		//when
		expenseDeleter.delete(mockExpense);

		//then
		verify(expenseRepository, times(1)).delete(eq(mockExpense));
	}
}