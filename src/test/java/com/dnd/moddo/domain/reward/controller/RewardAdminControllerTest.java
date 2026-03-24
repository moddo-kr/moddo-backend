package com.dnd.moddo.domain.reward.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.dnd.moddo.auth.model.exception.UserPermissionException;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.reward.application.RewardService;
import com.dnd.moddo.reward.presentation.RewardAdminController;

@ExtendWith(MockitoExtension.class)
class RewardAdminControllerTest {

	@Mock
	private RewardService rewardService;

	@Mock
	private QuerySettlementService querySettlementService;

	@InjectMocks
	private RewardAdminController rewardAdminController;

	@Test
	@DisplayName("관리자는 수동 보상 지급을 요청할 수 있다.")
	void manualGrantByAdmin() {
		when(querySettlementService.findIdByCode("group-code")).thenReturn(10L);

		ResponseEntity<Void> response = rewardAdminController.manualGrant(
			"group-code",
			2L,
			new LoginUserInfo(1L, "ADMIN")
		);

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		verify(rewardService).manualGrant(10L, 2L);
	}

	@Test
	@DisplayName("관리자가 아니면 수동 보상 지급을 요청할 수 없다.")
	void manualGrantForbiddenWhenNotAdmin() {
		assertThatThrownBy(() -> rewardAdminController.manualGrant(
			"group-code",
			2L,
			new LoginUserInfo(1L, "USER")
		)).isInstanceOf(UserPermissionException.class);

		verify(rewardService, never()).manualGrant(anyLong(), anyLong());
	}
}
