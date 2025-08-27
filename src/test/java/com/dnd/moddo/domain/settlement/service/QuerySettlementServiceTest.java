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

import com.dnd.moddo.domain.appointmentMember.entity.AppointmentMember;
import com.dnd.moddo.domain.appointmentMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.settlement.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.exception.GroupNotFoundException;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementReader;
import com.dnd.moddo.domain.settlement.service.implementation.SettlementValidator;
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
	private AppointmentMember appointmentMember;

	@BeforeEach
	void setUp() {
		settlement = GroupTestFactory.createDefault();
		appointmentMember = new AppointmentMember("김완숙", 1, settlement, false, ExpenseRole.MANAGER);

		setField(settlement, "id", 1L);
	}

	@Test
	@DisplayName("그룹 상세 정보를 정상적으로 조회할 수 있다.")
	void FindOne_Success() {
		// Given
		when(settlementReader.read(anyLong())).thenReturn(settlement);
		when(settlementReader.findByGroup(settlement.getId())).thenReturn(List.of(appointmentMember));
		doNothing().when(settlementValidator).checkGroupAuthor(settlement, 1L);

		// When
		GroupDetailResponse response = querySettlementService.findOne(settlement.getId(), 1L);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(settlement.getId());
		assertThat(response.groupName()).isEqualTo(settlement.getName());
		assertThat(response.members()).hasSize(1);
		assertThat(response.members().get(0).name()).isEqualTo(appointmentMember.getName());

		verify(settlementReader, times(1)).read(1L);
		verify(settlementReader, times(1)).findByGroup(settlement.getId());
		verify(settlementValidator, times(1)).checkGroupAuthor(settlement, 1L);
	}

	@Test
	@DisplayName("그룹 작성자가 아닐 경우 예외가 발생한다.")
	void FindOne_Failure_WhenNotGroupAuthor() {
		// Given
		when(settlementReader.read(anyLong())).thenReturn(settlement);
		doThrow(new RuntimeException("Not an author")).when(settlementValidator).checkGroupAuthor(settlement, 2L);

		// When & Then
		assertThatThrownBy(() -> querySettlementService.findOne(1L, 2L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Not an author");

		verify(settlementReader, times(1)).read(1L);
		verify(settlementValidator, times(1)).checkGroupAuthor(settlement, 2L);
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
		GroupHeaderResponse expectedResponse = new GroupHeaderResponse(settlement.getName(), 1000L,
			LocalDateTime.now().plusDays(1), settlement.getBank(), settlement.getAccountNumber());
		when(settlementReader.findByHeader(settlement.getId())).thenReturn(expectedResponse);

		// When
		GroupHeaderResponse response = querySettlementService.findBySettlementHeader(settlement.getId());

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
}
