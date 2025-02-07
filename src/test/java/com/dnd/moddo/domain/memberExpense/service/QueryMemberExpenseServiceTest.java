package com.dnd.moddo.domain.memberExpense.service;

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
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;

@ExtendWith(MockitoExtension.class)
class QueryMemberExpenseServiceTest {
	@Mock
	private MemberExpenseReader memberExpenseReader;
	@InjectMocks
	private QueryMemberExpenseService queryMemberExpenseService;

	private Group mockGroup;
	private Expense mockExpense;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
		mockExpense = new Expense(mockGroup, 20000L, "투썸플레이스", 0, LocalDate.of(2025, 02, 03));

	}

	@DisplayName("지출내역이 유효할때 지출내역의 참여자별 지출내역 조회에 성공한다.")
	@Test
	void findAllByExpenseId() {
		//given
		Long expenseId = 1L;
		GroupMember mockGroupMember1 = new GroupMember("박완숙", mockGroup);
		GroupMember mockGroupMember2 = new GroupMember("김반숙", mockGroup);

		List<MemberExpense> expectedMemberExpense = List.of(
			new MemberExpense(mockExpense, mockGroupMember1, 15000L),
			new MemberExpense(mockExpense, mockGroupMember2, 5000L)
		);

		when(memberExpenseReader.findAllByExpenseId(eq(expenseId))).thenReturn(expectedMemberExpense);

		//when
		List<MemberExpenseResponse> responses = queryMemberExpenseService.findAllByExpenseId(expenseId);

		//then
		assertThat(responses).isNotNull();
		assertThat(responses.size()).isEqualTo(expectedMemberExpense.size());
		assertThat(responses.get(0).name()).isEqualTo("박완숙");
		assertThat(responses.get(0).amount()).isEqualTo(15000L);

	}

}