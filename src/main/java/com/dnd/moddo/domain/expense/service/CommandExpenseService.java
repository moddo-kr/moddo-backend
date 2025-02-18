package com.dnd.moddo.domain.expense.service;

import java.util.List;

import com.dnd.moddo.domain.expense.dto.request.ExpenseImageRequest;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.group.service.implementation.GroupValidator;
import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseCreator;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseDeleter;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseUpdater;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.service.CommandMemberExpenseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandExpenseService {
	private final ExpenseReader expenseReader;
	private final ExpenseCreator expenseCreator;
	private final ExpenseUpdater expenseUpdater;
	private final ExpenseDeleter expenseDeleter;
	private final CommandMemberExpenseService commandMemberExpenseService;
	private final GroupReader groupReader;
	private final GroupValidator groupValidator;

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

	public ExpenseResponse update(Long expenseId, ExpenseRequest request) {
		Expense expense = expenseUpdater.update(expenseId, request);
		List<MemberExpenseResponse> memberExpenseResponses = commandMemberExpenseService.update(expenseId,
			request.memberExpenses());
		return ExpenseResponse.of(expense, memberExpenseResponses);

	}

	public void updateImgUrl(Long userId, Long groupId, Long expenseId, ExpenseImageRequest request) {
		Group group = groupReader.read(groupId);
		groupValidator.checkGroupAuthor(group, userId);
		expenseUpdater.updateImgUrl(expenseId, request);
	}


	public void delete(Long expenseId) {
		Expense expense = expenseReader.findByExpenseId(expenseId);
		//TODO 삭제하는 사람이 정산자인지 확인 로직 필요
		commandMemberExpenseService.deleteAllByExpenseId(expenseId);
		expenseDeleter.delete(expense);
	}
}
