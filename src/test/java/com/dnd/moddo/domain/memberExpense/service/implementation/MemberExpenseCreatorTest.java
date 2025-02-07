package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;

@ExtendWith(MockitoExtension.class)
class MemberExpenseCreatorTest {
	@Mock
	private MemberExpenseRepository memberExpenseRepository;
	@InjectMocks
	private MemberExpenseCreator memberExpenseCreator;

	private Expense expense;
	private GroupMember groupMember;
	private MemberExpenseRequest memberExpenseRequest;
	private Group mockGroup;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
		expense = new Expense(mockGroup, 20000L, "투썸플레이스", 0, LocalDate.of(2025, 02, 03));
		groupMember = new GroupMember("박완수", mockGroup.getId());
		memberExpenseRequest = mock(MemberExpenseRequest.class);
	}

	@DisplayName("지출내역, 참여자 정보가 모두 유효할 때 참여자 지출 내역 생성에 성공한다.")
	@Test
	void createMemberExpenseSuccess() {
		//given
		MemberExpense mockMemberExpense = new MemberExpense(expense, groupMember, memberExpenseRequest.amount());
		when(memberExpenseRequest.toEntity(expense, groupMember)).thenReturn(mockMemberExpense);
		when(memberExpenseRepository.save(any(MemberExpense.class))).thenReturn(mockMemberExpense);

		//when
		MemberExpense result = memberExpenseCreator.create(expense, groupMember, memberExpenseRequest);

		//then
		assertThat(result).isEqualTo(mockMemberExpense);
		verify(memberExpenseRepository, times(1)).save(any(MemberExpense.class));

	}
}