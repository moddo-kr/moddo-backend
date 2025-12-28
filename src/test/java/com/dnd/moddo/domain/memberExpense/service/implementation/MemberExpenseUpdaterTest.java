package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.MemberExpenseUpdater;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.infrastructure.MemberExpenseRepository;
import com.dnd.moddo.event.presentation.request.MemberExpenseRequest;

@ExtendWith(MockitoExtension.class)
class MemberExpenseUpdaterTest {
	@Mock
	private MemberExpenseRepository memberExpenseRepository;
	@InjectMocks
	private MemberExpenseUpdater memberExpenseUpdater;

	@DisplayName("지출 내역 수정 시 유효한 값이 들어오면 지출 내역 수정에 성공한다.")
	@Test
	void update_Success() {
		//given
		Long expectedAmount = 5000L;

		MemberExpense memberExpense = new MemberExpense(1L, mock(Member.class), 15000L);
		MemberExpenseRequest request = new MemberExpenseRequest(1L, expectedAmount);

		when(memberExpenseRepository.save(memberExpense)).thenReturn(any(MemberExpense.class));
		//when
		memberExpenseUpdater.update(memberExpense, request);

		//then
		assertThat(memberExpense.getAmount()).isEqualTo(expectedAmount);
		verify(memberExpenseRepository, times(1)).save(memberExpense);
	}

}