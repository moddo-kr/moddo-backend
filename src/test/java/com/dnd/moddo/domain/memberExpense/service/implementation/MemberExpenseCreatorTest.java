package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

	@DisplayName("지출내역, 참여자 정보가 모두 유효할 때 참여자 지출 내역 생성에 성공한다.")
	@Test
	void createMemberExpenseSuccess() {
		//given
		Group mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
		Expense expense = new Expense(mockGroup.getId(), 20000L, "투썸플레이스", LocalDate.of(2025, 02, 03));
		GroupMember mockGroupMember = new GroupMember("박완수", mockGroup);
		MemberExpenseRequest memberExpenseRequest = mock(MemberExpenseRequest.class);

		MemberExpense mockMemberExpense = new MemberExpense(expense, mockGroupMember, memberExpenseRequest.amount());
		when(memberExpenseRequest.toEntity(expense, mockGroupMember)).thenReturn(mockMemberExpense);
		when(memberExpenseRepository.save(any(MemberExpense.class))).thenReturn(mockMemberExpense);

		//when
		MemberExpense result = memberExpenseCreator.create(expense, mockGroupMember, memberExpenseRequest);

		//then
		assertThat(result).isEqualTo(mockMemberExpense);
		verify(memberExpenseRepository, times(1)).save(any(MemberExpense.class));

	}
}