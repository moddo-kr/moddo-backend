package com.dnd.moddo.domain.settlement.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.exception.GroupNotFoundException;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;

@ExtendWith(MockitoExtension.class)
class SettlementReaderTest {

	@Mock
	private SettlementRepository settlementRepository;

	@Mock
	private ExpenseRepository expenseRepository;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private SettlementReader settlementReader;

	@Test
	@DisplayName("그룹 ID를 통해 그룹을 정상적으로 조회할 수 있다.")
	void readGroup_Success() {
		// Given
		Long groupId = 1L;
		Settlement mockSettlement = mock(Settlement.class);

		when(settlementRepository.getById(anyLong())).thenReturn(mockSettlement);

		// When
		Settlement result = settlementReader.read(groupId);

		// Then
		assertThat(result).isNotNull();
		verify(settlementRepository, times(1)).getById(groupId);
	}

	@Test
	@DisplayName("그룹을 통해 그룹 멤버 목록을 정상적으로 조회할 수 있다.")
	void findBySettlement_Success() {
		// Given
		Settlement mockSettlement = mock(Settlement.class);
		when(mockSettlement.getId()).thenReturn(1L);
		List<Member> mockMembers = List.of(mock(Member.class), mock(Member.class));

		when(memberRepository.findBySettlementId(anyLong())).thenReturn(mockMembers);

		// When
		List<Member> result = settlementReader.findBySettlement(mockSettlement.getId());

		// Then
		assertThat(result).hasSize(2);
		verify(memberRepository, times(1)).findBySettlementId(mockSettlement.getId());
	}

	@Test
	@DisplayName("그룹 ID를 통해 그룹 헤더 정보를 정상적으로 조회할 수 있다.")
	void findByHeader_Success() {
		// Given
		Long groupId = 1L;
		Settlement mockSettlement = mock(Settlement.class);
		when(mockSettlement.getName()).thenReturn("모임 이름");
		when(mockSettlement.getBank()).thenReturn("은행");
		when(mockSettlement.getAccountNumber()).thenReturn("1234-1234");
		when(settlementRepository.getById(anyLong())).thenReturn(mockSettlement);

		Long totalAmount = 1000L;
		when(expenseRepository.sumAmountBySettlement(any(Settlement.class))).thenReturn(totalAmount);

		// When
		SettlementHeaderResponse result = settlementReader.findByHeader(groupId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.groupName()).isEqualTo("모임 이름");
		assertThat(result.totalAmount()).isEqualTo(1000L);
		assertThat(result.bank()).isEqualTo("은행");
		assertThat(result.accountNumber()).isEqualTo("1234-1234");
		verify(settlementRepository, times(1)).getById(groupId);
		verify(expenseRepository, times(1)).sumAmountBySettlement(mockSettlement);
	}

	@DisplayName("group code로 group Id를 찾을 수 있다.")
	@Test
	void whenValidGroupCode_thenReturnsGroupId() {
		//given
		Long expected = 1L;
		when(settlementRepository.getIdByCode(anyString())).thenReturn(expected);
		//when
		Long result = settlementReader.findIdByGroupCode("code");
		//then
		assertThat(result).isEqualTo(expected);
		verify(settlementRepository, times(1)).getIdByCode(anyString());
	}

	@DisplayName("group code로 group id를 찾을 수 없을때 예외가 발생한다.")
	@Test
	void whenInvalidGroupCode_thenThrowsException() {
		//given
		when(settlementRepository.getIdByCode(anyString())).thenThrow(new GroupNotFoundException("code"));
		//when & then
		assertThatThrownBy(() -> settlementReader.findIdByGroupCode("code"))
			.isInstanceOf(GroupNotFoundException.class)
			.hasMessageContaining("code");

		verify(settlementRepository, times(1)).getIdByCode(anyString());
	}
}
