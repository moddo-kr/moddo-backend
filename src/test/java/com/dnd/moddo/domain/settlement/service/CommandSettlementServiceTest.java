package com.dnd.moddo.domain.settlement.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.event.application.command.CommandMemberService;
import com.dnd.moddo.event.application.command.CommandSettlementService;
import com.dnd.moddo.event.application.impl.SettlementCreator;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementUpdater;
import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.domain.member.ExpenseRole;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.request.SettlementAccountRequest;
import com.dnd.moddo.event.presentation.request.request.SettlementPasswordRequest;
import com.dnd.moddo.event.presentation.request.request.SettlementRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.SettlementPasswordResponse;
import com.dnd.moddo.event.presentation.response.SettlementResponse;
import com.dnd.moddo.event.presentation.response.SettlementSaveResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

@ExtendWith(MockitoExtension.class)
class CommandSettlementServiceTest {

	@Mock
	private SettlementCreator settlementCreator;

	@Mock
	private SettlementUpdater settlementUpdater; // 추가

	@Mock
	private SettlementReader settlementReader;

	@Mock
	private SettlementValidator settlementValidator;
	@Mock
	private JwtProvider jwtProvider;
	@Mock
	private CommandMemberService commandMemberService;
	@InjectMocks
	private CommandSettlementService commandSettlementService;

	private SettlementRequest settlementRequest;
	private SettlementAccountRequest settlementAccountRequest;
	private Settlement settlement;
	private SettlementResponse settlementResponse;
	private SettlementSaveResponse expectedResponse;

	@BeforeEach
	void setUp() {
		settlementRequest = new SettlementRequest("GroupName", "password123");
		settlementResponse = new SettlementResponse(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
			"bank",
			"1234-1234", LocalDateTime.now().plusDays(1));
		settlementAccountRequest = new SettlementAccountRequest("newBank", "5678-5678");
		expectedResponse = new SettlementSaveResponse("groupToken", mock(MemberResponse.class));
		settlement = mock(Settlement.class);
	}

	@Test
	@DisplayName("그룹과 총무를 생성할 수 있다.")
	void createSettlement() {
		// Given
		MemberResponse memberResponse = new MemberResponse(1L, ExpenseRole.MANAGER,
			"김모또", null, true,
			LocalDateTime.now());

		when(settlementCreator.createSettlement(any(SettlementRequest.class), anyLong())).thenReturn(settlement);
		when(settlement.getCode()).thenReturn("code");
		when(commandMemberService.createManager(any(), any())).thenReturn(memberResponse);

		// When
		SettlementSaveResponse response = commandSettlementService.createSettlement(settlementRequest, 1L);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.groupToken()).isEqualTo("code");
		assertThat(response.manager().role()).isEqualTo(ExpenseRole.MANAGER);

		verify(settlementCreator, times(1)).createSettlement(any(SettlementRequest.class), anyLong());
		verify(commandMemberService, times(1)).createManager(any(), any());
	}

	@Test
	@DisplayName("그룹의 계좌 정보를 업데이트할 수 있다.")
	void updateGroupAccount() {
		// Given
		when(settlementReader.read(anyLong())).thenReturn(settlement);
		when(settlementUpdater.updateAccount(any(SettlementAccountRequest.class), anyLong())).thenReturn(settlement);
		doNothing().when(settlementValidator).checkSettlementAuthor(any(Settlement.class), anyLong());

		// When
		SettlementResponse result = commandSettlementService.updateAccount(settlementAccountRequest,
			settlement.getWriter(),
			settlement.getId());

		// Then
		assertThat(result).isNotNull();
		verify(settlementReader, times(1)).read(anyLong());
		verify(settlementValidator, times(1)).checkSettlementAuthor(any(Settlement.class), anyLong());
		verify(settlementUpdater, times(1)).updateAccount(any(SettlementAccountRequest.class), anyLong());
	}

	@Test
	@DisplayName("올바른 비밀번호를 입력하면 확인 메시지를 반환한다.")
	void VerifyPassword_Success() {
		// Given
		SettlementPasswordRequest request = new SettlementPasswordRequest("correctPassword");
		SettlementPasswordResponse expectedResponse = SettlementPasswordResponse.from("확인되었습니다.");

		when(settlementReader.read(settlement.getId())).thenReturn(settlement);
		doNothing().when(settlementValidator).checkSettlementAuthor(settlement, 1L);
		when(settlementValidator.checkSettlementPassword(request, settlement.getPassword())).thenReturn(
			expectedResponse);

		// When
		SettlementPasswordResponse response = commandSettlementService.isPasswordMatch(settlement.getId(), 1L, request);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.status()).isEqualTo("확인되었습니다.");

		verify(settlementReader, times(1)).read(settlement.getId());
		verify(settlementValidator, times(1)).checkSettlementAuthor(settlement, 1L);
		verify(settlementValidator, times(1)).checkSettlementPassword(request, settlement.getPassword());
	}

	@Test
	@DisplayName("잘못된 비밀번호를 입력하면 예외가 발생한다.")
	void VerifyPassword_Fail_WrongPassword() {
		// Given
		SettlementPasswordRequest request = new SettlementPasswordRequest("wrongPassword");
		String storedPassword = "correctPassword";

		when(settlementReader.read(settlement.getId())).thenReturn(settlement);
		doNothing().when(settlementValidator).checkSettlementAuthor(settlement, 1L);
		when(settlement.getPassword()).thenReturn(storedPassword);

		doThrow(new RuntimeException("비밀번호가 일치하지 않습니다."))
			.when(settlementValidator).checkSettlementPassword(request, storedPassword);

		// When & Then
		assertThatThrownBy(() -> commandSettlementService.isPasswordMatch(settlement.getId(), 1L, request))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("비밀번호가 일치하지 않습니다.");

		verify(settlementReader, times(1)).read(settlement.getId());
		verify(settlementValidator, times(1)).checkSettlementAuthor(settlement, 1L);
		verify(settlementValidator, times(1)).checkSettlementPassword(request, storedPassword);
	}
}
