package com.dnd.moddo.event.presentation;

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

import com.dnd.moddo.event.application.command.CommandExpenseService;
import com.dnd.moddo.event.application.query.QueryExpenseService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.presentation.request.ExpenseImageRequest;
import com.dnd.moddo.event.presentation.request.ExpenseRequest;
import com.dnd.moddo.event.presentation.request.ExpensesRequest;
import com.dnd.moddo.event.presentation.response.ExpenseDetailsResponse;
import com.dnd.moddo.event.presentation.response.ExpenseResponse;
import com.dnd.moddo.event.presentation.response.ExpensesResponse;
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
	private final QuerySettlementService querySettlementService;

	@PostMapping
	public ResponseEntity<ExpensesResponse> saveExpenses(
		@RequestParam("groupToken") String code,
		@Valid @RequestBody ExpensesRequest request) {
		Long settlementId = querySettlementService.findIdByCode(code);
		ExpensesResponse response = commandExpenseService.createExpenses(settlementId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<ExpensesResponse> getAllBySettlementId(@RequestParam("groupToken") String code) {
		Long settlementId = querySettlementService.findIdByCode(code);
		ExpensesResponse response = queryExpenseService.findAllBySettlementId(settlementId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{expenseId}")
	public ResponseEntity<ExpenseResponse> getByExpenseId(@PathVariable("expenseId") Long expenseId) {
		ExpenseResponse response = queryExpenseService.findOneByExpenseId(expenseId);
		return ResponseEntity.ok(response);

	}

	@GetMapping("/details")
	public ResponseEntity<ExpenseDetailsResponse> getExpenseDetailsBySettlementId(
		@RequestParam("groupToken") String code) {
		Long settlementId = querySettlementService.findIdByCode(code);
		ExpenseDetailsResponse response = queryExpenseService.findAllExpenseDetailsBySettlementId(settlementId);
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
		@RequestParam("groupToken") String code,
		@PathVariable("expenseId") Long expenseId,
		@RequestBody ExpenseImageRequest expenseImageRequest) {
		Long userId = jwtService.getUserId(request);
		Long settlementId = querySettlementService.findIdByCode(code);
		commandExpenseService.updateImgUrl(userId, settlementId, expenseId, expenseImageRequest);
	}
}
