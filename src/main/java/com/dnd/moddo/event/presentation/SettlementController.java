package com.dnd.moddo.event.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.application.command.CommandSettlementService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.domain.settlement.type.SettlementStatus;
import com.dnd.moddo.event.presentation.request.SettlementAccountRequest;
import com.dnd.moddo.event.presentation.request.SettlementRequest;
import com.dnd.moddo.event.presentation.response.SettlementDetailResponse;
import com.dnd.moddo.event.presentation.response.SettlementHeaderResponse;
import com.dnd.moddo.event.presentation.response.SettlementListResponse;
import com.dnd.moddo.event.presentation.response.SettlementResponse;
import com.dnd.moddo.event.presentation.response.SettlementSaveResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class SettlementController {
	private final CommandSettlementService commandSettlementService;
	private final QuerySettlementService querySettlementService;

	@PostMapping
	public ResponseEntity<SettlementSaveResponse> saveSettlement(HttpServletRequest request,
		@RequestBody SettlementRequest settlementRequest,
		@LoginUser LoginUserInfo loginUser
	) {
		SettlementSaveResponse response = commandSettlementService.createSettlement(settlementRequest,
			loginUser.userId());
		return ResponseEntity.ok(response);
	}

	@PutMapping("/account")
	public ResponseEntity<SettlementResponse> updateAccount(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@RequestBody SettlementAccountRequest settlementAccountRequest,
		@LoginUser LoginUserInfo loginUser
	) {
		Long settlementId = querySettlementService.findIdByCode(code);

		SettlementResponse response = commandSettlementService.updateAccount(settlementAccountRequest,
			loginUser.userId(),
			settlementId);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<SettlementDetailResponse> getSettlement(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@LoginUser LoginUserInfo loginUser) {
		Long settlementId = querySettlementService.findIdByCode(code);
		SettlementDetailResponse response = querySettlementService.findOne(settlementId, loginUser.userId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/header")
	public ResponseEntity<SettlementHeaderResponse> getHeader(
		@RequestParam("groupToken") String code) {
		Long settlementId = querySettlementService.findIdByCode(code);

		SettlementHeaderResponse response = querySettlementService.findBySettlementHeader(settlementId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/header/no-cache")
	public ResponseEntity<SettlementHeaderResponse> getHeaderNoCache(
		@RequestParam("groupToken") String code) {
		Long settlementId = querySettlementService.findIdByCodeNoCache(code);

		SettlementHeaderResponse response = querySettlementService.findBySettlementHeader(settlementId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/list")
	public ResponseEntity<?> search(
		@LoginUser LoginUserInfo user,
		@RequestParam(required = false) SettlementStatus status
	) {
		List<SettlementListResponse> response = querySettlementService.search(user.userId(), status);
		return ResponseEntity.ok(response);
	}
}
