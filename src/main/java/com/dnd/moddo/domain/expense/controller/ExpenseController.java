package com.dnd.moddo.domain.expense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
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
	public ResponseEntity<ExpensesResponse> saveExpenses(@RequestParam("meetId") String token,
		@RequestBody ExpensesRequest request) {
		Long meetId = 1L;
		ExpensesResponse response = commandExpenseService.createExpense(meetId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<ExpensesResponse> getAllByMeetId(@RequestParam("meetId") String token) {
		Long meetId = 1L;
		ExpensesResponse response = queryExpenseService.findAllByMeetId(meetId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> getByExpenseId(@RequestParam("meetId") String token,
		@PathVariable("expenseId") Long expenseId) {
		Long meetId = 1L;
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(meetId, expenseId);
		return ResponseEntity.ok(response);

	}

	@PutMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> updateByExpenseId(@RequestParam("meetId") String token,
		@PathVariable("expenseId") Long expenseId) {
		Long meetId = 1L;
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(meetId, expenseId);
		return ResponseEntity.ok(response);

	}
}
