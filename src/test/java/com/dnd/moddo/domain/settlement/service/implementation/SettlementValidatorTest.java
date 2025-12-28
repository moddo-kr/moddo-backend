package com.dnd.moddo.domain.settlement.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.domain.settlement.exception.GroupNotAuthorException;
import com.dnd.moddo.event.domain.settlement.exception.InvalidPasswordException;
import com.dnd.moddo.event.presentation.request.SettlementPasswordRequest;
import com.dnd.moddo.event.presentation.response.SettlementPasswordResponse;

class SettlementValidatorTest {

	private SettlementValidator settlementValidator;

	@Mock
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		settlementValidator = new SettlementValidator(passwordEncoder, null); // GroupRepository는 필요 없으면 null 처리
	}

	@Test
	@DisplayName("그룹 작성자와 요청 사용자가 같으면 예외가 발생하지 않는다.")
	void checkSettlementAuthor_Success() {
		// Given
		Settlement settlement = mock(Settlement.class);
		Long writer = 1L;
		when(settlement.isWriter(writer)).thenReturn(true);

		// When & Then
		settlementValidator.checkSettlementAuthor(settlement, writer);
	}

	@Test
	@DisplayName("그룹 작성자와 요청 사용자가 다르면 GroupNotAuthorException 예외가 발생한다.")
	void checkSettlementAuthor_Fail() {
		Settlement settlement = mock(Settlement.class);
		Long writer = 1L;
		when(settlement.isWriter(writer)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> settlementValidator.checkSettlementAuthor(settlement, writer))
			.isInstanceOf(GroupNotAuthorException.class);
	}

	@Test
	@DisplayName("올바른 비밀번호를 입력하면 확인 메시지를 반환한다.")
	void checkSettlementPassword_Success() {
		// Given
		String rawPassword = "correctPassword";
		String encodedPassword = "$2a$10$WzHqXjA4oH8lTxH9m6Q7se1k2dG0B1h7U4Fv0t5y8LdHwWx7uy6MS";
		SettlementPasswordRequest request = new SettlementPasswordRequest(rawPassword);

		when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

		// When
		SettlementPasswordResponse response = settlementValidator.checkSettlementPassword(request, encodedPassword);

		// Then
		assertThat(response.status()).isEqualTo("확인되었습니다.");
	}

	@Test
	@DisplayName("잘못된 비밀번호를 입력하면 InvalidPasswordException 예외가 발생한다.")
	void checkSettlementPassword_Fail() {
		// Given
		String rawPassword = "wrongPassword";
		String encodedPassword = "$2a$10$WzHqXjA4oH8lTxH9m6Q7se1k2dG0B1h7U4Fv0t5y8LdHwWx7uy6MS";
		SettlementPasswordRequest request = new SettlementPasswordRequest(rawPassword);

		when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> settlementValidator.checkSettlementPassword(request, encodedPassword))
			.isInstanceOf(InvalidPasswordException.class);
	}
}
