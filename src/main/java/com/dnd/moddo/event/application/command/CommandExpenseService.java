package com.dnd.moddo.event.application.command;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.application.impl.ExpenseCreator;
import com.dnd.moddo.event.application.impl.ExpenseDeleter;
import com.dnd.moddo.event.application.impl.ExpenseReader;
import com.dnd.moddo.event.application.impl.ExpenseUpdater;
import com.dnd.moddo.event.application.impl.SettlementReader;
import com.dnd.moddo.event.application.impl.SettlementValidator;
import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.domain.settlement.Settlement;
import com.dnd.moddo.event.presentation.request.ExpenseImageRequest;
import com.dnd.moddo.event.presentation.request.ExpenseRequest;
import com.dnd.moddo.event.presentation.request.ExpensesRequest;
import com.dnd.moddo.event.presentation.response.ExpenseResponse;
import com.dnd.moddo.event.presentation.response.ExpensesResponse;
import com.dnd.moddo.event.presentation.response.MemberExpenseResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandExpenseService {
	private final ExpenseReader expenseReader;
	private final ExpenseCreator expenseCreator;
	private final ExpenseUpdater expenseUpdater;
	private final ExpenseDeleter expenseDeleter;
	private final CommandMemberExpenseService commandMemberExpenseService;
	private final SettlementReader settlementReader;
	private final SettlementValidator settlementValidator;

	public ExpensesResponse createExpenses(Long groupId, ExpensesRequest request) {
		List<ExpenseResponse> expenses = request.expenses()
			.stream()
			.map(e -> createExpense(groupId, e))
			.toList();
		return new ExpensesResponse(expenses);
	}

	private ExpenseResponse createExpense(Long groupId, ExpenseRequest request) {
		Expense expense = expenseCreator.create(groupId, request);

		List<MemberExpenseResponse> memberExpenseResponses = commandMemberExpenseService.create(expense.getId(),
			request.memberExpenses());
		return ExpenseResponse.of(expense, memberExpenseResponses);
	}

	public ExpenseResponse update(Long userId, Long expenseId, Long settlementId, ExpenseRequest request) {
		Expense expense = expenseReader.findByExpenseId(expenseId);

		expense.validateSettlement(settlementId);

		Settlement settlement = settlementReader.read(settlementId);
		settlementValidator.checkSettlementAuthor(settlement, userId);

		expense = expenseUpdater.update(expenseId, request);
		List<MemberExpenseResponse> memberExpenseResponses = commandMemberExpenseService.update(expenseId,
			request.memberExpenses());
		return ExpenseResponse.of(expense, memberExpenseResponses);

	}

	public void updateImgUrl(Long userId, Long groupId, Long expenseId, ExpenseImageRequest request) {
		Settlement settlement = settlementReader.read(groupId);
		settlementValidator.checkSettlementAuthor(settlement, userId);
		expenseUpdater.updateImgUrl(expenseId, request);
	}

	public void delete(Long userId, Long expenseId, Long settlementId) {
		Expense expense = expenseReader.findByExpenseId(expenseId);

		expense.validateSettlement(settlementId);

		Settlement settlement = settlementReader.read(settlementId);
		settlementValidator.checkSettlementAuthor(settlement, userId);

		commandMemberExpenseService.deleteAllByExpenseId(expenseId);
		expenseDeleter.delete(expense);
	}
}
