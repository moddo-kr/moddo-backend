package com.dnd.moddo.event.application.query;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.ExpenseReader;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.presentation.response.ExpenseDetailResponse;
import com.dnd.moddo.event.presentation.response.ExpenseDetailsResponse;
import com.dnd.moddo.event.presentation.response.ExpenseResponse;
import com.dnd.moddo.event.presentation.response.ExpensesResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryExpenseService {
	private final ExpenseReader expenseReader;
	private final QueryMemberExpenseService queryMemberExpenseService;

	public ExpensesResponse findAllBySettlementId(Long settlementId) {
		List<Expense> expenses = expenseReader.findAllBySettlementId(settlementId);
		return new ExpensesResponse(
			expenses.stream()
				.map(expense ->
					ExpenseResponse.of(expense, queryMemberExpenseService.findAllByExpenseId(expense.getId()))
				).toList()
		);
	}

	public ExpenseResponse findOneByExpenseId(Long expenseId) {
		Expense expense = expenseReader.findByExpenseId(expenseId);
		return ExpenseResponse.of(expense);
	}

	public ExpenseDetailsResponse findAllExpenseDetailsBySettlementId(Long settlementId) {

		List<Expense> expenses = expenseReader.findAllBySettlementId(settlementId);
		List<Long> expenseIds = expenses.stream().map(Expense::getId).toList();

		Map<Long, List<String>> memberNameByExpenseId = queryMemberExpenseService.getMemberNamesByExpenseIds(
			expenseIds);

		return new ExpenseDetailsResponse(
			expenses.stream()
				.map(expense ->
					ExpenseDetailResponse.of(expense, memberNameByExpenseId.get(expense.getId()))
				).toList()
		);
	}
}
