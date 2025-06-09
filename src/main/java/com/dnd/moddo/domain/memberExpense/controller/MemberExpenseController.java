package com.dnd.moddo.domain.memberExpense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.group.service.QueryGroupService;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersExpenseResponse;
import com.dnd.moddo.domain.memberExpense.service.QueryMemberExpenseService;
import com.dnd.moddo.global.jwt.service.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/member-expenses")
@RestController
public class MemberExpenseController {
	private final QueryMemberExpenseService queryMemberExpenseService;
	private final JwtService jwtService;
	private final QueryGroupService queryGroupService;

	@GetMapping
	public ResponseEntity<GroupMembersExpenseResponse> getMemberExpensesDetails(
		@RequestParam("groupToken") String code
	) {
		Long groupId = queryGroupService.findIdByCode(code);
		GroupMembersExpenseResponse response = queryMemberExpenseService.findMemberExpenseDetailsByGroupId(groupId);
		return ResponseEntity.ok(response);
	}
}
