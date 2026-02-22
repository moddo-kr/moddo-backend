package com.dnd.moddo.domain.settlement.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;

import java.time.LocalDateTime;
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
import com.dnd.moddo.event.domain.settlement.type.SettlementSortType;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.infrastructure.MemberRepository;
import com.dnd.moddo.event.infrastructure.SettlementQueryRepository;
import com.dnd.moddo.event.infrastructure.SettlementRepository;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.dnd.moddo.event.presentation.response.SettlementShareResponse;

@ExtendWith(MockitoExtension.class)
class SettlementReaderTest {

	@Mock
	private SettlementRepository settlementRepository;

	@Mock
	private ExpenseRepository expenseRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private SettlementQueryRepository settlementQueryRepository;

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

	@Test
	@DisplayName("사용자와 상태를 통해 정산 리스트를 정상적으로 조회할 수 있다.")
	void findListByUserAndStatus_Success() {
		// Given
		Long userId = 1L;
		SettlementStatus status = SettlementStatus.IN_PROGRESS;
		SettlementSortType sortType = SettlementSortType.LATEST;
		int limit = 20;

		List<SettlementListResponse> mockList = List.of(
			new SettlementListResponse(
				1L,
				"groupCode",
				"모또 모임",
				10000L,
				5L,
				3L,
				LocalDateTime.now(),
				LocalDateTime.now()
			),
			new SettlementListResponse(
				2L,
				"groupCode2",
				"두번째 모임",
				50000L,
				4L,
				4L,
				LocalDateTime.now(),
				LocalDateTime.now()
			)
		);

		when(settlementQueryRepository.findByUserAndStatus(
			userId,
			status,
			sortType,
			limit
		)).thenReturn(mockList);

		// When
		List<SettlementListResponse> result =
			settlementReader.findListByUserIdAndStatus(
				userId,
				status,
				sortType,
				limit
			);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).groupId()).isEqualTo(1L);

		verify(settlementQueryRepository, times(1))
			.findByUserAndStatus(userId, status, sortType, limit);
	}

	@Test
	@DisplayName("사용자 ID로 공유용 정산 리스트를 정상적으로 조회할 수 있다.")
	void findShareListByUserId_Success() {
		// Given
		Long userId = 1L;

		List<SettlementShareResponse> mockList = List.of(
			new SettlementShareResponse(
				1L,
				"모또 모임",
				"groupCode",
				LocalDateTime.now(),
				null
			),
			new SettlementShareResponse(
				2L,
				"두번째 모임",
				"groupCode2",
				LocalDateTime.now(),
				LocalDateTime.now()
			)
		);

		when(settlementQueryRepository.findBySettlementList(userId))
			.thenReturn(mockList);

		// When
		List<SettlementShareResponse> result =
			settlementReader.findShareListByUserId(userId);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).settlementId()).isEqualTo(1L);
		assertThat(result.get(0).name()).isEqualTo("모또 모임");

		verify(settlementQueryRepository, times(1))
			.findBySettlementList(userId);
	}
}
