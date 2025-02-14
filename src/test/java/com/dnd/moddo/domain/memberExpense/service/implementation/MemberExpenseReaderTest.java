package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;

@ExtendWith(MockitoExtension.class)
class MemberExpenseReaderTest {
	@Mock
	private MemberExpenseRepository memberExpenseRepository;
	@InjectMocks
	private MemberExpenseReader memberExpenseReader;

	private Group mockGroup;
	private GroupMember mockGroupMember;

	@BeforeEach
	void setUp() {
		mockGroup = new Group("group 1", 1L, "1234", LocalDateTime.now(), LocalDateTime.now().plusMinutes(1),
			"은행", "계좌");
		mockGroupMember = new GroupMember("박완숙", mockGroup, ExpenseRole.MANAGER);
	}

	@DisplayName("지출내역이 존재하면 해당 지출내역의 참여자별 지출내역을 조회에 성공한다.")
	@Test
	void findAllByExpenseIdS() {
		//given
		Long expenseId = 1L;
		List<MemberExpense> expectedMemberExpense = List.of(new MemberExpense(expenseId, mockGroupMember, 15000L));

		when(memberExpenseRepository.findByExpenseId(eq(expenseId))).thenReturn(expectedMemberExpense);

		//when
		List<MemberExpense> result = memberExpenseReader.findAllByExpenseId(expenseId);

		//then
		assertThat(result).isNotNull();
		assertThat(result.get(0).getAmount()).isEqualTo(15000L);
		assertThat(result.get(0).getGroupMember()).isEqualTo(mockGroupMember);

		verify(memberExpenseRepository, times(1)).findByExpenseId(eq(expenseId));
	}
}