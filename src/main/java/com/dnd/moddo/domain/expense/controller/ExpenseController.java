package com.dnd.moddo.domain.expense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.service.CommandExpenseService;
import com.dnd.moddo.domain.expense.service.QueryExpenseService;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersExpenseResponse;
import com.dnd.moddo.global.jwt.service.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
@RestController
public class ExpenseController {

	private final CommandExpenseService commandExpenseService;
	private final QueryExpenseService queryExpenseService;
	private final JwtService jwtService;

	@PostMapping
	public ResponseEntity<ExpensesResponse> saveExpenses(
		@RequestParam("groupToken") String groupToken,
		@RequestBody ExpensesRequest request) {
		Long groupId = jwtService.getGroupId(groupToken);
		ExpensesResponse response = commandExpenseService.createExpenses(groupId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<ExpensesResponse> getAllByGroupId(@RequestParam("groupToken") String groupToken) {
		Long groupId = jwtService.getGroupId(groupToken);
		ExpensesResponse response = queryExpenseService.findAllByGroupId(groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> getByExpenseId(@PathVariable("expenseId") Long expenseId) {
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(expenseId);
		return ResponseEntity.ok(response);

	}

	@PutMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> updateByExpenseId(@PathVariable("expenseId") Long expenseId,
		@RequestBody ExpenseRequest request) {
		ExpenseResponse response = commandExpenseService.update(expenseId, request);
		return ResponseEntity.ok(response);

	}

	@DeleteMapping("/{expenseId}")
	public ResponseEntity<Void> deleteByExpenseId(@PathVariable("expenseId") Long expenseId) {
		commandExpenseService.delete(expenseId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/settlement")
	public ResponseEntity<GroupMembersExpenseResponse> getSettlement(
		@RequestParam("groupToken") String groupToken
	) {
		Long groupId = jwtService.getGroupId(groupToken);
		GroupMembersExpenseResponse response = queryExpenseService.findSettlementByGroupId(groupId);
		return ResponseEntity.ok(response);
	}
}
