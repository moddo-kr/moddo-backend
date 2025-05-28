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

import com.dnd.moddo.domain.expense.dto.request.ExpenseImageRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseDetailsResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.service.CommandExpenseService;
import com.dnd.moddo.domain.expense.service.QueryExpenseService;
import com.dnd.moddo.domain.group.service.QueryGroupService;
import com.dnd.moddo.global.common.annotation.VerifyManagerPermission;
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
@RestController
public class ExpenseController {

	private final CommandExpenseService commandExpenseService;
	private final QueryExpenseService queryExpenseService;
	private final JwtService jwtService;
	private final QueryGroupService queryGroupService;

	@PostMapping
	public ResponseEntity<ExpensesResponse> saveExpenses(
		@RequestParam("code") String code,
		@Valid @RequestBody ExpensesRequest request) {
		Long groupId = queryGroupService.findIdByCode(code);
		ExpensesResponse response = commandExpenseService.createExpenses(groupId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<ExpensesResponse> getAllByGroupId(@RequestParam("code") String code) {
		Long groupId = queryGroupService.findIdByCode(code);
		ExpensesResponse response = queryExpenseService.findAllByGroupId(groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> getByExpenseId(@PathVariable("expenseId") Long expenseId) {
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(expenseId);
		return ResponseEntity.ok(response);

	}

	@GetMapping("/details")
	public ResponseEntity<ExpenseDetailsResponse> getExpenseDetailsByGroupId(
		@RequestParam("code") String code) {
		Long groupId = queryGroupService.findIdByCode(code);
		ExpenseDetailsResponse response = queryExpenseService.findAllExpenseDetailsByGroupId(groupId);
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@PutMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> updateByExpenseId(@PathVariable("expenseId") Long expenseId,
		@RequestBody ExpenseRequest request) {
		ExpenseResponse response = commandExpenseService.update(expenseId, request);
		return ResponseEntity.ok(response);

	}

	@VerifyManagerPermission
	@DeleteMapping("/{expenseId}")
	public ResponseEntity<Void> deleteByExpenseId(@PathVariable("expenseId") Long expenseId) {
		commandExpenseService.delete(expenseId);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/img/{expenseId}")
	public void updateImgUrl(HttpServletRequest request,
		@RequestParam("code") String code,
		@PathVariable("expenseId") Long expenseId,
		@RequestBody ExpenseImageRequest expenseImageRequest) {
		Long userId = jwtService.getUserId(request);
		Long groupId = queryGroupService.findIdByCode(code);
		commandExpenseService.updateImgUrl(userId, groupId, expenseId, expenseImageRequest);
	}
}
