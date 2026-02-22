package com.dnd.moddo.domain.settlement.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.exception.GroupNotFoundException;
import com.dnd.moddo.event.domain.settlement.type.SettlementSortType;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.presentation.request.SearchSettlementListRequest;
import com.dnd.moddo.event.presentation.response.SettlementDetailResponse;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class QuerySettlementServiceTest {

	@InjectMocks
	private QuerySettlementService querySettlementService;

	@Mock
	private SettlementReader settlementReader;

	@Mock
	private SettlementValidator settlementValidator;

	private Settlement settlement;
	private Member member;

	@BeforeEach
	void setUp() {
		settlement = GroupTestFactory.createDefault();
		member = Member.builder()
			.name("김완숙")
			.profileId(1)
			.settlement(settlement)
			.role(ExpenseRole.PARTICIPANT)
			.user(null)
			.build();

		setField(settlement, "id", 1L);
	}

	@Test
	@DisplayName("그룹 상세 정보를 정상적으로 조회할 수 있다.")
	void FindOne_Success() {
		// Given
		when(settlementReader.read(anyLong())).thenReturn(settlement);
		when(settlementReader.findBySettlement(settlement.getId())).thenReturn(List.of(member));
		doNothing().when(settlementValidator).checkSettlementAuthor(settlement, 1L);

		// When
		SettlementDetailResponse response = querySettlementService.findOne(settlement.getId(), 1L);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(settlement.getId());
		assertThat(response.groupName()).isEqualTo(settlement.getName());
		assertThat(response.members()).hasSize(1);
		assertThat(response.members().get(0).name()).isEqualTo(member.getName());

		verify(settlementReader, times(1)).read(1L);
		verify(settlementReader, times(1)).findBySettlement(settlement.getId());
		verify(settlementValidator, times(1)).checkSettlementAuthor(settlement, 1L);
	}

	@Test
	@DisplayName("그룹 작성자가 아닐 경우 예외가 발생한다.")
	void FindOne_Failure_WhenNotGroupAuthor() {
		// Given
		when(settlementReader.read(anyLong())).thenReturn(settlement);
		doThrow(new RuntimeException("Not an author")).when(settlementValidator).checkSettlementAuthor(settlement, 2L);

		// When & Then
		assertThatThrownBy(() -> querySettlementService.findOne(1L, 2L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Not an author");

		verify(settlementReader, times(1)).read(1L);
		verify(settlementValidator, times(1)).checkSettlementAuthor(settlement, 2L);
	}

	@Test
	@DisplayName("그룹을 찾을 수 없을 경우 예외가 발생한다.")
	void FindOne_Failure_WhenGroupNotFound() {
		// Given
		when(settlementReader.read(anyLong())).thenThrow(new RuntimeException("Group not found"));

		// When & Then
		assertThatThrownBy(() -> querySettlementService.findOne(1L, 1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Group not found");

		verify(settlementReader, times(1)).read(1L);
	}

	@Test
	@DisplayName("그룹 헤더를 정상적으로 조회할 수 있다.")
	void FindBySettlementHeader_Success() {
		// Given
		SettlementHeaderResponse expectedResponse = new SettlementHeaderResponse(settlement.getName(), 1000L,
			LocalDateTime.now().plusDays(1), settlement.getBank(), settlement.getAccountNumber());
		when(settlementReader.findByHeader(settlement.getId())).thenReturn(expectedResponse);

		// When
		SettlementHeaderResponse response = querySettlementService.findBySettlementHeader(settlement.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response.groupName()).isEqualTo(settlement.getName());
		assertThat(response.bank()).isEqualTo(settlement.getBank());
		assertThat(response.accountNumber()).isEqualTo(settlement.getAccountNumber());

		verify(settlementReader, times(1)).findByHeader(settlement.getId());
	}

	@Test
	@DisplayName("그룹 헤더를 찾을 수 없을 경우 예외가 발생한다.")
	void FindBySettlementHeader_Failure_WhenHeaderNotFound() {
		// Given
		when(settlementReader.findByHeader(anyLong())).thenThrow(new RuntimeException("Header not found"));

		// When & Then
		assertThatThrownBy(() -> querySettlementService.findBySettlementHeader(1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Header not found");

		verify(settlementReader, times(1)).findByHeader(1L);
	}

	@DisplayName("group code가 유효할 때 group Id를 찾을 수 있다.")
	@Test
	void FindByGroupId_Success() {
		//given
		Long expected = 1L;
		when(settlementReader.findIdByGroupCode(anyString())).thenReturn(expected);
		//when
		Long result = querySettlementService.findIdByCode("code");
		//then
		assertThat(result).isEqualTo(expected);
		verify(settlementReader, times(1)).findIdByGroupCode(anyString());
	}

	@DisplayName("group code가 존재하지 않을때 예외가 발생한다..")
	@Test
	void FindByGroupId_ThrowException_WhenCodeNotFound() {
		//given
		when(settlementReader.findIdByGroupCode(anyString())).thenThrow(new GroupNotFoundException("code"));
		//when & then
		assertThatThrownBy(() -> querySettlementService.findIdByCode("code"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("code");

		verify(settlementReader, times(1)).findIdByGroupCode(anyString());
	}

	@DisplayName("group code가 유효할 때 group Id를 찾을 수 있다.")
	@Test
	void FindByGroupIdNoCache_Success() {
		//given
		Long expected = 1L;
		when(settlementReader.findIdByGroupCode(anyString())).thenReturn(expected);
		//when
		Long result = querySettlementService.findIdByCodeNoCache("code");
		//then
		assertThat(result).isEqualTo(expected);
		verify(settlementReader, times(1)).findIdByGroupCode(anyString());
	}

	@DisplayName("group code가 존재하지 않을때 예외가 발생한다..")
	@Test
	void FindByGroupIdNoCache_ThrowException_WhenCodeNotFound() {
		//given
		when(settlementReader.findIdByGroupCode(anyString())).thenThrow(new GroupNotFoundException("code"));
		//when & then
		assertThatThrownBy(() -> querySettlementService.findIdByCodeNoCache("code"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("code");

		verify(settlementReader, times(1)).findIdByGroupCode(anyString());
	}

	@Test
	@DisplayName("status가 null이면 ALL로 조회한다.")
	void search_WhenStatusIsNull_ShouldUseAll() {
		// given
		Long userId = 1L;

		SearchSettlementListRequest request =
			new SearchSettlementListRequest(
				null,
				SettlementSortType.LATEST,
				20
			);

		List<SettlementListResponse> mockList = List.of();

		when(settlementReader.findListByUserIdAndStatus(
			userId,
			SettlementStatus.ALL,
			SettlementSortType.LATEST,
			20
		)).thenReturn(mockList);

		// when
		List<SettlementListResponse> result =
			querySettlementService.search(userId, request);

		// then
		assertThat(result).isEmpty();

		verify(settlementReader, times(1))
			.findListByUserIdAndStatus(
				userId,
				SettlementStatus.ALL,
				SettlementSortType.LATEST,
				20
			);
	}

	@Test
	@DisplayName("status가 존재하면 해당 status로 조회한다.")
	void search_WhenStatusExists_ShouldUseGivenStatus() {
		// given
		Long userId = 1L;

		SearchSettlementListRequest request =
			new SearchSettlementListRequest(
				SettlementStatus.IN_PROGRESS,
				SettlementSortType.LATEST,
				20
			);

		List<SettlementListResponse> mockList = List.of(
			new SettlementListResponse(
				1L,
				"groupCode",
				"모또 모임",
				5L,
				3L,
				LocalDateTime.now(),
				null
			)
		);

		when(settlementReader.findListByUserIdAndStatus(
			userId,
			SettlementStatus.IN_PROGRESS,
			SettlementSortType.LATEST,
			20
		)).thenReturn(mockList);

		// when
		List<SettlementListResponse> result =
			querySettlementService.search(userId, request);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).groupId()).isEqualTo(1L);

		verify(settlementReader, times(1))
			.findListByUserIdAndStatus(
				userId,
				SettlementStatus.IN_PROGRESS,
				SettlementSortType.LATEST,
				20
			);
	}

	@Test
	@DisplayName("limit가 null이면 기본값 10을 사용한다.")
	void search_WhenLimitIsNull_ShouldUseDefaultLimit() {
		// given
		Long userId = 1L;

		SearchSettlementListRequest request =
			new SearchSettlementListRequest(
				SettlementStatus.ALL,
				SettlementSortType.LATEST,
				null
			);

		when(settlementReader.findListByUserIdAndStatus(
			userId,
			SettlementStatus.ALL,
			SettlementSortType.LATEST,
			10
		)).thenReturn(List.of());

		// when
		querySettlementService.search(userId, request);

		// then
		verify(settlementReader, times(1))
			.findListByUserIdAndStatus(
				userId,
				SettlementStatus.ALL,
				SettlementSortType.LATEST,
				10
			);
	}
}
