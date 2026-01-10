package com.dnd.moddo.domain.expense.service.implementation;

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

import com.dnd.moddo.event.application.impl.ExpenseUpdater;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.presentation.request.ExpenseImageRequest;
import com.dnd.moddo.event.presentation.request.ExpenseRequest;

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

	@DisplayName("지출내역이 존재하면 이미지 URL 목록을 업데이트할 수 있다.")
	@Test
	void updateImgUrlSuccess() {
		//given
		Long expenseId = 1L;
		Expense oldExpense = mock(Expense.class);
		List<String> newImages = List.of("image1.jpg", "image2.jpg", "image3.jpg");

		when(expenseRepository.getById(eq(expenseId))).thenReturn(oldExpense);

		ExpenseImageRequest request = mock(ExpenseImageRequest.class);
		when(request.images()).thenReturn(newImages);

		//when
		Expense updatedExpense = expenseUpdater.updateImgUrl(expenseId, request);

		//then
		verify(oldExpense, times(1)).updateImgUrl(eq(newImages));
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