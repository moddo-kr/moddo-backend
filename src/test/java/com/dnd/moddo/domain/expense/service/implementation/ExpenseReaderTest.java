package com.dnd.moddo.domain.expense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.group.entity.Group;

@ExtendWith(MockitoExtension.class)
class ExpenseReaderTest {
	@Mock
	private ExpenseRepository expenseRepository;

	@InjectMocks
	private ExpenseReader expenseReader;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌", LocalDateTime.now().plusDays(1));
	}

	@DisplayName("모임이 존재하면 모임에 해당하는 지출내역을 모두 조회할 수 있다.")
	@Test
	void findAllByGroupId() {
		//given
		Long groupId = mockGroup.getId();
		List<Expense> mockExpenses = List.of(
			new Expense(mockGroup, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03)),
			new Expense(mockGroup, 35000L, "보드게임카페", LocalDate.of(2025, 02, 03))
		);

		when(expenseRepository.findByGroupIdOrderByDateAsc(eq(groupId))).thenReturn(mockExpenses);

		//when
		List<Expense> result = expenseReader.findAllByGroupId(groupId);

		assertThat(result).isNotNull();
		assertThat(result.size()).isEqualTo(mockExpenses.size());
		assertThat(result.get(0).getContent()).isEqualTo("투썸플레이스");

		//then
		verify(expenseRepository, times(1)).findByGroupIdOrderByDateAsc(eq(groupId));
	}

	@DisplayName("지출내역이 존재하면 해당 지출내역을 조회할 수 있다.")
	@Test
	void findByExpenseIdSuccess() {
		//given
		Long expenseId = 1L;
		Expense mockExpense = new Expense(mockGroup, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));

		when(expenseRepository.getById(eq(expenseId))).thenReturn(mockExpense);
		//when
		Expense result = expenseReader.findByExpenseId(expenseId);
		//then
		assertThat(result.getContent()).isEqualTo("투썸플레이스");
		assertThat(result.getAmount()).isEqualTo(20000);
		assertThat(result.getDate()).isEqualTo("2025-02-03");

		verify(expenseRepository, times(1)).getById(eq(expenseId));
	}

	@DisplayName("지출내역이 존재하지 않으면 조회시 예외가 발생한다.")
	@Test
	void findByExpenseIdNotFoundExpense() {
		//given
		Long expenseId = 1L;

		when(expenseRepository.getById(eq(expenseId))).thenThrow(new ExpenseNotFoundException(expenseId));
		//when & then
		assertThatThrownBy(() -> {
			expenseReader.findByExpenseId(expenseId);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");
	}
}