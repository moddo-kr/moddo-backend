package com.dnd.moddo.domain.settlement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.settlement.dto.request.SettlementAccountRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementRequest;
import com.dnd.moddo.domain.settlement.dto.response.SettlementDetailResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementHeaderResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementPasswordResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementResponse;
import com.dnd.moddo.domain.settlement.dto.response.SettlementSaveResponse;
import com.dnd.moddo.domain.settlement.service.CommandSettlementService;
import com.dnd.moddo.domain.settlement.service.QuerySettlementService;
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class SettlementController {
	private final CommandSettlementService commandSettlementService;
	private final JwtService jwtService;
	private final QuerySettlementService querySettlementService;

	@PostMapping
	public ResponseEntity<SettlementSaveResponse> saveSettlement(HttpServletRequest request,
		@RequestBody SettlementRequest settlementRequest) {
		Long userId = jwtService.getUserId(request);
		SettlementSaveResponse response = commandSettlementService.createSettlement(settlementRequest, userId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/account")
	public ResponseEntity<SettlementResponse> updateAccount(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@RequestBody SettlementAccountRequest settlementAccountRequest) {
		Long userId = jwtService.getUserId(request);
		Long settlementId = querySettlementService.findIdByCode(code);

		SettlementResponse response = commandSettlementService.updateAccount(settlementAccountRequest, userId,
			settlementId);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<SettlementDetailResponse> getSettlement(
		HttpServletRequest request,
		@RequestParam("groupToken") String code) {
		Long userId = jwtService.getUserId(request);
		Long settlementId = querySettlementService.findIdByCode(code);

		SettlementDetailResponse response = querySettlementService.findOne(settlementId, userId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/password")
	public ResponseEntity<SettlementPasswordResponse> isPasswordMatch(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@RequestBody SettlementPasswordRequest settlementPasswordRequest) {
		Long userId = jwtService.getUserId(request);
		Long settlementId = querySettlementService.findIdByCode(code);

		SettlementPasswordResponse response = commandSettlementService.isPasswordMatch(settlementId, userId,
			settlementPasswordRequest);
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
}
