package com.dnd.moddo.domain.expense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseValidator;

@ExtendWith(MockitoExtension.class)
class ExpenseCreatorTest {
	@Mock
	private ExpenseRepository expenseRepository;
	@Mock
	private GroupRepository groupRepository;
	@Mock
	private MemberExpenseValidator memberExpenseValidator;
	@InjectMocks
	private ExpenseCreator expenseCreator;

	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌", LocalDateTime.now().plusDays(1));
	}

	@DisplayName("모임이 존재하면 지출내역을 생성에 성공한다.")
	@Test
	void createSuccess() {
		//given
		Long groupId = mockGroup.getId();
		when(groupRepository.getById(eq(groupId))).thenReturn(mockGroup);
		ExpenseRequest request = mock(ExpenseRequest.class);

		Expense mockExpense = new Expense(mockGroup, 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));
		when(expenseRepository.save(any())).thenReturn(mockExpense);
		doNothing().when(memberExpenseValidator).validateMembersArePartOfGroup(groupId, new ArrayList<>());
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