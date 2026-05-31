package com.dnd.moddo.event.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.application.query.QueryMemberExpenseService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.presentation.response.MembersExpenseResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/groups/{code}/member-expenses")
public class MemberExpenseController {

	private final QueryMemberExpenseService queryMemberExpenseService;
	private final QuerySettlementService querySettlementService;

	@GetMapping
	public ResponseEntity<MembersExpenseResponse> getMemberExpensesDetails(
		@PathVariable String code,
		@LoginUser LoginUserInfo loginUser
	) {
		Long settlementId = querySettlementService.findIdByCode(code);

		MembersExpenseResponse response =
			queryMemberExpenseService.findMemberExpenseDetailsBySettlementId(settlementId, loginUser.userId());

		return ResponseEntity.ok(response);
	}
}
