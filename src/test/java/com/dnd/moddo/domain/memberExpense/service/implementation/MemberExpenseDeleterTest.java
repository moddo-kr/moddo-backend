package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberExpenseDeleter;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.infrastructure.MemberExpenseRepository;

@ExtendWith(MockitoExtension.class)
class MemberExpenseDeleterTest {
	@Mock
	private MemberExpenseRepository memberExpenseRepository;
	@InjectMocks
	private MemberExpenseDeleter memberExpenseDeleter;

	@DisplayName("지출내역 id가 유효할때 지출내역과 연관되어있는 참여자 지출내역 모두를 삭제할 수 있다.")
	@Test
	void deleteAllByExpenseId_Success() {
		//given
		Long expenseId = 1L;
		MemberExpense memberExpense1 = mock(MemberExpense.class);
		MemberExpense memberExpense2 = mock(MemberExpense.class);
		List<MemberExpense> memberExpenses = List.of(memberExpense1, memberExpense2);

		when(memberExpenseRepository.findByExpenseId(eq(expenseId))).thenReturn(memberExpenses);
		doNothing().when(memberExpenseRepository).deleteAll(memberExpenses);

		//when
		memberExpenseDeleter.deleteAllByExpenseId(expenseId);

		//then
		verify(memberExpenseRepository, times(1)).findByExpenseId(eq(expenseId));
		verify(memberExpenseRepository, times(1)).deleteAll(memberExpenses);
	}

	@DisplayName("참여자별 지출내역을 하나씩 삭제할 수 있다.")
	@Test
	void deleteByMemberExpense_Success() {
		//given
		MemberExpense memberExpense = mock(MemberExpense.class);
		doNothing().when(memberExpenseRepository).delete(memberExpense);

		//when
		memberExpenseDeleter.deleteByMemberExpense(memberExpense);

		//then
		verify(memberExpenseRepository, times(1)).delete(any());
	}

	@DisplayName("참여자별 지출내역이 여러개 들어올때 한번에 삭제할 수 있다.")
	@Test
	void deleteByMemberExpenses_Success() {
		//given
		MemberExpense memberExpense1 = mock(MemberExpense.class);
		MemberExpense memberExpense2 = mock(MemberExpense.class);
		List<MemberExpense> memberExpenses = List.of(memberExpense1, memberExpense2);
		doNothing().when(memberExpenseRepository).deleteAll(memberExpenses);

		//when
		memberExpenseDeleter.deleteByMemberExpenses(memberExpenses);

		//then
		verify(memberExpenseRepository, times(1)).deleteAll(any());
	}
}