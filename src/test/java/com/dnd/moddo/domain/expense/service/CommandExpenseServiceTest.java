package com.dnd.moddo.domain.expense.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesUpdateOrderRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseCreator;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseDeleter;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseUpdater;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.service.CommandMemberExpenseService;
import com.dnd.moddo.domain.memberExpense.service.QueryMemberExpenseService;

@ExtendWith(MockitoExtension.class)
class CommandExpenseServiceTest {

	@Mock
	private ExpenseCreator expenseCreator;
	@Mock
	private ExpenseReader expenseReader;
	@Mock
	private ExpenseUpdater expenseUpdater;
	@Mock
	private ExpenseDeleter expenseDeleter;
	@Mock
	private CommandMemberExpenseService commandMemberExpenseService;
	@Mock
	private QueryMemberExpenseService queryMemberExpenseService;
	@InjectMocks
	private CommandExpenseService commandExpenseService;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
	}

	@DisplayName("모임이 존재할 때 여러 지출 내역 생성에 성공한다.")
	@Test
	void createExpense() {
		//given
		Long groupId = mockGroup.getId();

		//when(expenseReader.findMaxOrderForGroup(eq(groupId))).thenReturn(0);

		ExpenseRequest expenseRequest1 = new ExpenseRequest(20000L, "투썸플레이스", LocalDate.of(2025, 02, 03),
			new ArrayList<>());
		ExpenseRequest expenseRequest2 = new ExpenseRequest(100000L, "하이디라오", LocalDate.of(2025, 02, 03),
			new ArrayList<>());

		ExpensesRequest request = new ExpensesRequest(List.of(expenseRequest1, expenseRequest2));

		Expense expense1 = new Expense(mockGroup, 20000L, "투썸플레이스", 0, LocalDate.of(2025, 02, 03));
		Expense expense2 = new Expense(mockGroup, 100000L, "하이디라오", 1, LocalDate.of(2025, 02, 03));
		when(expenseCreator.create(eq(groupId), anyInt(), any(ExpenseRequest.class)))
			.thenReturn(expense1)
			.thenReturn(expense2);

		// When
		ExpensesResponse response = commandExpenseService.createExpenses(groupId, request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.expenses().size()).isEqualTo(2);
		assertThat(response.expenses().get(0).content()).isEqualTo("투썸플레이스");
		assertThat(response.expenses().get(0).date()).isEqualTo("2025-02-03");
	}

	@DisplayName("지출내역이 존재할 때 기존의 지출내역을 수정할 수 있다.")
	@Test
	void updateSuccess() {
		//given
		Long groupId = mockGroup.getId(), expenseId = 1L;
		Expense mockExpense = new Expense(mockGroup, 20000L, "투썸플레이스", 1, LocalDate.of(2025, 02, 03));
		ExpenseRequest expenseRequest = mock(ExpenseRequest.class);
		ExpenseResponse expectedResponse = ExpenseResponse.of(mockExpense);

		when(expenseUpdater.update(eq(expenseId), eq(expenseRequest))).thenReturn(mockExpense);
		// when
		ExpenseResponse response = commandExpenseService.update(expenseId, expenseRequest);

		//then
		assertThat(response).isNotNull();
		assertThat(response).isEqualTo(expectedResponse);

		verify(expenseUpdater, times(1)).update(expenseId, expenseRequest);
	}

	@DisplayName("지출내역이 존재할 때 기존의 지출내역의 순서를 변경할 수 있다.")
	@Test
	void updateOrder_Success_ValidRequest() {
		//given
		Expense expense1 = new Expense(mockGroup, 20000L, "expense 1", 1, LocalDate.of(2025, 02, 03));
		Expense expense2 = new Expense(mockGroup, 15000L, "expense 2", 2, LocalDate.of(2025, 02, 03));
		List<Expense> expectedExpenses = List.of(expense1, expense2);
		ExpensesUpdateOrderRequest request = new ExpensesUpdateOrderRequest(new ArrayList<>());

		when(expenseUpdater.updateOrder(request)).thenReturn(expectedExpenses);

		List<MemberExpenseResponse> responses1 = List.of(
			new MemberExpenseResponse(1L, ExpenseRole.MANAGER, "김모또", 15000L),
			new MemberExpenseResponse(2L, ExpenseRole.PARTICIPANT, "박완숙", 5000L));
		List<MemberExpenseResponse> responses2 = List.of(
			new MemberExpenseResponse(1L, ExpenseRole.MANAGER, "김모또", 15000L),
			new MemberExpenseResponse(2L, ExpenseRole.PARTICIPANT, "박완숙", 2000L));

		when(queryMemberExpenseService.findAllByExpenseId(any()))
			.thenReturn(responses1)
			.thenReturn(responses2);

		// when
		ExpensesResponse response = commandExpenseService.updateOrder(request);

		//then
		assertThat(response).isNotNull();
		assertThat(response.expenses().size()).isEqualTo(2);
		assertThat(response.expenses().get(0).content()).isEqualTo("expense 1");
		assertThat(response.expenses().get(0).memberExpenses().size()).isEqualTo(2);

		verify(expenseUpdater, times(1)).updateOrder(request);
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
		doNothing().when(expenseDeleter).delete(eq(expenseId));
		//when
		commandExpenseService.delete(expenseId);
		//then
		verify(expenseDeleter, times(1)).delete(eq(expenseId));
	}

	@DisplayName("삭제하려는 지출내역이 존재하지 않는다면 예외가 발생한다.")
	@Test
	void deleteNotFound() {
		//given
		Long expenseId = 1L;
		doThrow(new ExpenseNotFoundException(expenseId)).when(expenseDeleter).delete(eq(expenseId));
		//when & then
		assertThatThrownBy(() -> {
			commandExpenseService.delete(expenseId);
		}).hasMessage("해당 지출내역을 찾을 수 없습니다. (Expense ID: " + expenseId + ")");

	}
}