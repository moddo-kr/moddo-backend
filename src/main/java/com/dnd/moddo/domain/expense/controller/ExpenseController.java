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
import com.dnd.moddo.domain.expense.dto.request.ExpensesUpdateOrderRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.service.CommandExpenseService;
import com.dnd.moddo.domain.expense.service.QueryExpenseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
@RestController
public class ExpenseController {

	private final CommandExpenseService commandExpenseService;
	private final QueryExpenseService queryExpenseService;

	@PostMapping
	public ResponseEntity<ExpensesResponse> saveExpenses(@RequestParam("groupId") Long groupId,
		@RequestBody ExpensesRequest request) {
		ExpensesResponse response = commandExpenseService.createExpenses(groupId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<ExpensesResponse> getAllByGroupId(@RequestParam("groupId") Long groupId) {
		ExpensesResponse response = queryExpenseService.findAllByGroupId(groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> getByExpenseId(@RequestParam("groupId") Long groupId,
		@PathVariable("expenseId") Long expenseId) {
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(expenseId);
		return ResponseEntity.ok(response);

	}

	@PutMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> updateByExpenseId(@RequestParam("groupId") Long groupId,
		@PathVariable("expenseId") Long expenseId,
		@RequestBody ExpenseRequest request) {
		ExpenseResponse response = commandExpenseService.update(expenseId, request);
		return ResponseEntity.ok(response);

	}

	@PutMapping("/order")
	public ResponseEntity<ExpensesResponse> updateExpenseOrder(@RequestParam("groupId") Long groupId,
		@RequestBody ExpensesUpdateOrderRequest request) {
		ExpensesResponse response = commandExpenseService.updateOrder(request);
		return ResponseEntity.ok(response);

	}

	@DeleteMapping("/{expenseId}")
	public ResponseEntity<Void> deleteByExpenseId(@RequestParam("groupId") Long groupId,
		@PathVariable("expenseId") Long expenseId) {
		commandExpenseService.delete(expenseId);
		return ResponseEntity.noContent().build();
	}
}
