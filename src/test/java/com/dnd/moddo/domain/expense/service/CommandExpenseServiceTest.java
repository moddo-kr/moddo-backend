package com.dnd.moddo.domain.expense.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.command.CommandExpenseService;
import com.dnd.moddo.event.application.command.CommandMemberExpenseService;
import com.dnd.moddo.event.application.impl.ExpenseCreator;
import com.dnd.moddo.event.application.impl.ExpenseDeleter;
import com.dnd.moddo.event.application.impl.ExpenseReader;
import com.dnd.moddo.event.application.impl.ExpenseUpdater;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.ExpenseImageRequest;
import com.dnd.moddo.event.presentation.request.ExpenseRequest;
import com.dnd.moddo.event.presentation.request.ExpensesRequest;
import com.dnd.moddo.event.presentation.response.ExpenseResponse;
import com.dnd.moddo.event.presentation.response.ExpensesResponse;
import com.dnd.moddo.event.presentation.response.MemberExpenseResponse;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class CommandExpenseServiceTest {

	@Mock
	private ExpenseReader expenseReader;
	@Mock
	private ExpenseCreator expenseCreator;
	@Mock
	private ExpenseUpdater expenseUpdater;
	@Mock
	private ExpenseDeleter expenseDeleter;
	@Mock
	private SettlementReader settlementReader;
	@Mock
	private SettlementValidator settlementValidator;
	@Mock
	private CommandMemberExpenseService commandMemberExpenseService;
	@InjectMocks
	private CommandExpenseService commandExpenseService;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("모임이 존재할 때 여러 지출 내역 생성에 성공한다.")
	@Test
	void createExpense() {
		//given
		Long groupId = mockSettlement.getId();

		ExpenseRequest expenseRequest1 = new ExpenseRequest(20000L, "투썸플레이스", LocalDate.of(2025, 02, 03),
			new ArrayList<>());
		ExpenseRequest expenseRequest2 = new ExpenseRequest(100000L, "하이디라오", LocalDate.of(2025, 02, 03),
			new ArrayList<>());

		ExpensesRequest request = new ExpensesRequest(List.of(expenseRequest1, expenseRequest2));

		Expense expense1 = new Expense(mockSettlement, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));
		Expense expense2 = new Expense(mockSettlement, 100000L, "하이디라오", LocalDate.of(2025, 02, 03));
		when(expenseCreator.create(eq(groupId), any(ExpenseRequest.class)))
			.thenReturn(expense1)
			.thenReturn(expense2);

		// When
		ExpensesResponse response = commandExpenseService.createExpenses(groupId, request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.expenses()).hasSize(2);
		assertThat(response.expenses().get(0).content()).isEqualTo("투썸플레이스");
		assertThat(response.expenses().get(0).date()).isEqualTo("2025-02-03");
	}

	@DisplayName("지출내역이 존재할 때 기존의 지출내역을 수정할 수 있다.")
	@Test
	void updateSuccess() {
		//given
		Long groupId = mockSettlement.getId(), expenseId = 1L;
		Expense mockExpense = new Expense(mockSettlement, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));
		ExpenseRequest expenseRequest = mock(ExpenseRequest.class);
		ExpenseResponse expectedResponse = ExpenseResponse.of(mockExpense);

		MemberExpenseResponse memberExpenseResponse1 = mock(MemberExpenseResponse.class);
		MemberExpenseResponse memberExpenseResponse2 = mock(MemberExpenseResponse.class);

		when(expenseUpdater.update(eq(expenseId), eq(expenseRequest))).thenReturn(mockExpense);
		when(commandMemberExpenseService.update(eq(expenseId), any())).thenReturn(
			List.of(memberExpenseResponse1, memberExpenseResponse2));
		// when
		ExpenseResponse response = commandExpenseService.update(expenseId, expenseRequest);

		//then
		assertThat(response).isNotNull();
		assertThat(response.content()).isEqualTo("투썸플레이스");
		assertThat(response.memberExpenses().size()).isEqualTo(2);
		verify(expenseUpdater, times(1)).update(expenseId, expenseRequest);
	}

	@DisplayName("업데이트하려는 지출 내역을 찾을 수 없을때 예외를 발생시킨다.")
	@Test
	void updateNotFound() {
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
	void deleteSuccess() {
		//given
		Long expenseId = 1L;
		Expense mockExpense = mock(Expense.class);

		when(expenseReader.findByExpenseId(eq(expenseId))).thenReturn(mockExpense);
		doNothing().when(commandMemberExpenseService).deleteAllByExpenseId(eq(expenseId));
		doNothing().when(expenseDeleter).delete(eq(mockExpense));

		//when
		commandExpenseService.delete(expenseId);

		//then
		verify(commandMemberExpenseService, times(1)).deleteAllByExpenseId(eq(expenseId));
		verify(expenseDeleter, times(1)).delete(eq(mockExpense));
	}

	@DisplayName("삭제하려는 지출내역이 존재하지 않는다면 예외가 발생한다.")
	@Test
	void deleteNotFound() {
		//given
		Long expenseId = 1L;
		doThrow(new ExpenseNotFoundException(expenseId)).when(expenseReader).findByExpenseId(eq(expenseId));

		//when & then
		assertThatThrownBy(() -> {
			commandExpenseService.delete(expenseId);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");

	}

	@DisplayName("지출 내역의 이미지 URL을 업데이트할 수 있다.")
	@Test
	void updateImgUrlSuccess() {
		// given
		Long userId = this.mockSettlement.getWriter(), groupId = this.mockSettlement.getId(), expenseId = 1L;
		ExpenseImageRequest request = mock(ExpenseImageRequest.class);
		Settlement mockSettlement = mock(Settlement.class);

		when(settlementReader.read(groupId)).thenReturn(mockSettlement);
		doNothing().when(settlementValidator).checkSettlementAuthor(mockSettlement, userId);

		// when
		commandExpenseService.updateImgUrl(userId, groupId, expenseId, request);

		// then
		verify(settlementReader, times(1)).read(groupId);
		verify(settlementValidator, times(1)).checkSettlementAuthor(mockSettlement, userId);
		verify(expenseUpdater, times(1)).updateImgUrl(expenseId, request);
	}

}