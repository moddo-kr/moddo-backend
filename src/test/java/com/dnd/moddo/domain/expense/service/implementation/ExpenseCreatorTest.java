package com.dnd.moddo.domain.expense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.ExpenseCreator;
import com.dnd.moddo.event.application.impl.MemberExpenseValidator;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.request.ExpenseRequest;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class ExpenseCreatorTest {
	@Mock
	private ExpenseRepository expenseRepository;
	@Mock
	private SettlementRepository settlementRepository;
	@Mock
	private MemberExpenseValidator memberExpenseValidator;
	@InjectMocks
	private ExpenseCreator expenseCreator;

	private Settlement mockSettlement;

	@BeforeEach
	void setUp() {
		mockSettlement = GroupTestFactory.createDefault();
	}

	@DisplayName("모임이 존재하면 지출내역을 생성에 성공한다.")
	@Test
	void createSuccess() {
		//given
		Long groupId = mockSettlement.getId();
		when(settlementRepository.getById(eq(groupId))).thenReturn(mockSettlement);
		ExpenseRequest request = mock(ExpenseRequest.class);

		Expense mockExpense = new Expense(mockSettlement, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));
		when(expenseRepository.save(any())).thenReturn(mockExpense);
		doNothing().when(memberExpenseValidator).validateMembersArePartOfSettlement(groupId, new ArrayList<>());
		//when
		Expense result = expenseCreator.create(groupId, request);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEqualTo("투썸플레이스");
		assertThat(result.getAmount()).isEqualTo(20000L);
		assertThat(result.getDate()).isEqualTo("2025-02-03");
		verify(expenseRepository, times(1)).save(any());
	}
}