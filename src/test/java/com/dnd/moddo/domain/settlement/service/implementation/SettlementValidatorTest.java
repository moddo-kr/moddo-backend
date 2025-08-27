package com.dnd.moddo.domain.settlement.service.implementation;

import com.dnd.moddo.domain.settlement.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.settlement.entity.Settlement;
import com.dnd.moddo.domain.settlement.exception.GroupNotAuthorException;
import com.dnd.moddo.domain.settlement.exception.InvalidPasswordException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
	void checkGroupAuthor_Success() {
		// Given
		Settlement settlement = mock(Settlement.class);
		Long writer = 1L;
		when(settlement.isWriter(writer)).thenReturn(true);

		// When & Then
		settlementValidator.checkGroupAuthor(settlement, writer);
	}

	@Test
	@DisplayName("그룹 작성자와 요청 사용자가 다르면 GroupNotAuthorException 예외가 발생한다.")
	void checkGroupAuthor_Fail() {
		Settlement settlement = mock(Settlement.class);
		Long writer = 1L;
		when(settlement.isWriter(writer)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> settlementValidator.checkGroupAuthor(settlement, writer))
			.isInstanceOf(GroupNotAuthorException.class);
	}

	@Test
	@DisplayName("올바른 비밀번호를 입력하면 확인 메시지를 반환한다.")
	void checkGroupPassword_Success() {
		// Given
		String rawPassword = "correctPassword";
		String encodedPassword = "$2a$10$WzHqXjA4oH8lTxH9m6Q7se1k2dG0B1h7U4Fv0t5y8LdHwWx7uy6MS";
		GroupPasswordRequest request = new GroupPasswordRequest(rawPassword);

		when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

		// When
		GroupPasswordResponse response = settlementValidator.checkGroupPassword(request, encodedPassword);

		// Then
		assertThat(response.status()).isEqualTo("확인되었습니다.");
	}

	@Test
	@DisplayName("잘못된 비밀번호를 입력하면 InvalidPasswordException 예외가 발생한다.")
	void checkGroupPassword_Fail() {
		// Given
		String rawPassword = "wrongPassword";
		String encodedPassword = "$2a$10$WzHqXjA4oH8lTxH9m6Q7se1k2dG0B1h7U4Fv0t5y8LdHwWx7uy6MS";
		GroupPasswordRequest request = new GroupPasswordRequest(rawPassword);

		when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> settlementValidator.checkGroupPassword(request, encodedPassword))
			.isInstanceOf(InvalidPasswordException.class);
	}
}
