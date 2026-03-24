package com.dnd.moddo.reward.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.model.exception.UserPermissionException;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.reward.application.RewardService;
import com.dnd.moddo.user.domain.Authority;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/rewards")
public class RewardAdminController {
	private final RewardService rewardService;
	private final QuerySettlementService querySettlementService;

	@PostMapping("/groups/{code}/users/{userId}")
	public ResponseEntity<Void> manualGrant(
		@PathVariable String code,
		@PathVariable Long userId,
		@LoginUser LoginUserInfo loginUser
	) {
		validateAdmin(loginUser);
		Long settlementId = querySettlementService.findIdByCode(code);
		rewardService.manualGrant(settlementId, userId);
		return ResponseEntity.ok().build();
	}

	private void validateAdmin(LoginUserInfo loginUser) {
		if (!Authority.ADMIN.name().equals(loginUser.role())) {
			throw new UserPermissionException();
		}
	}
}
