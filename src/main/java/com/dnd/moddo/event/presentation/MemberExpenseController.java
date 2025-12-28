package com.dnd.moddo.event.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.event.application.query.QueryMemberExpenseService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.presentation.response.MembersExpenseResponse;
import com.dnd.moddo.global.jwt.service.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member-expenses")
@RestController
public class MemberExpenseController {
	private final QueryMemberExpenseService queryMemberExpenseService;
	private final JwtService jwtService;
	private final QuerySettlementService querySettlementService;

	@GetMapping
	public ResponseEntity<MembersExpenseResponse> getMemberExpensesDetails(
		@RequestParam("groupToken") String code
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		MembersExpenseResponse response = queryMemberExpenseService.findMemberExpenseDetailsBySettlementId(
			settlementId);
		return ResponseEntity.ok(response);
	}
}
