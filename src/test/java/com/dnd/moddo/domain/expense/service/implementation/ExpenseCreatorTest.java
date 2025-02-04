package com.dnd.moddo.domain.expense.service.implementation;

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

import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseCreatorTest {
	@Mock
	private ExpenseRepository expenseRepository;
	@InjectMocks
	private ExpenseCreator expenseCreator;

	@DisplayName("모임이 존재하면 지출내역을 생성에 성공한다.")
	@Test
	void createSuccess() {
		//given
		Long meetId = 1L;
		ExpensesRequest request = new ExpensesRequest(new ArrayList<>());
		List<Expense> mockExpense = List.of(
			new Expense(meetId, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03)),
			new Expense(meetId, 35000L, "보드게임카페", LocalDate.of(2025, 02, 03))
		);
		when(expenseRepository.saveAll(any())).thenReturn(mockExpense);

		//when
		List<Expense> result = expenseCreator.create(meetId, request);

		//then
		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(mockExpense.size());
		assertThat(result.get(0).getContent()).isEqualTo("투썸플레이스");

		verify(expenseRepository, times(1)).saveAll(any());
	}
}