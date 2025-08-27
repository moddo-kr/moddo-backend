package com.dnd.moddo.domain.settlement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.settlement.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementAccountRequest;
import com.dnd.moddo.domain.settlement.dto.request.SettlementRequest;
import com.dnd.moddo.domain.settlement.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupResponse;
import com.dnd.moddo.domain.settlement.dto.response.GroupSaveResponse;
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
	public ResponseEntity<GroupSaveResponse> saveSettlement(HttpServletRequest request,
		@RequestBody SettlementRequest settlementRequest) {
		Long userId = jwtService.getUserId(request);
		GroupSaveResponse response = commandSettlementService.createGroup(settlementRequest, userId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/account")
	public ResponseEntity<GroupResponse> updateAccount(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@RequestBody SettlementAccountRequest settlementAccountRequest) {
		Long userId = jwtService.getUserId(request);
		Long groupId = querySettlementService.findIdByCode(code);

		GroupResponse response = commandSettlementService.updateAccount(settlementAccountRequest, userId, groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<GroupDetailResponse> getSettlement(
		HttpServletRequest request,
		@RequestParam("groupToken") String code) {
		Long userId = jwtService.getUserId(request);
		Long groupId = querySettlementService.findIdByCode(code);

		GroupDetailResponse response = querySettlementService.findOne(groupId, userId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/password")
	public ResponseEntity<GroupPasswordResponse> isPasswordMatch(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@RequestBody GroupPasswordRequest groupPasswordRequest) {
		Long userId = jwtService.getUserId(request);
		Long groupId = querySettlementService.findIdByCode(code);

		GroupPasswordResponse response = commandSettlementService.isPasswordMatch(groupId, userId,
			groupPasswordRequest);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/header")
	public ResponseEntity<GroupHeaderResponse> getHeader(
		@RequestParam("groupToken") String code) {
		Long groupId = querySettlementService.findIdByCode(code);

		GroupHeaderResponse response = querySettlementService.findBySettlementHeader(groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/header/no-cache")
	public ResponseEntity<GroupHeaderResponse> getHeaderNoCache(
		@RequestParam("groupToken") String code) {
		Long groupId = querySettlementService.findIdByCodeNoCache(code);

		GroupHeaderResponse response = querySettlementService.findBySettlementHeader(groupId);
		return ResponseEntity.ok(response);
	}
}
