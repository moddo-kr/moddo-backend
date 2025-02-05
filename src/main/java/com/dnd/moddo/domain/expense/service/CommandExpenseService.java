package com.dnd.moddo.domain.expense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseCreator;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseDeleter;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseUpdater;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseCreator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandExpenseService {
	private final ExpenseCreator expenseCreator;
	private final ExpenseUpdater expenseUpdater;
	private final ExpenseDeleter expenseDeleter;
	private final MemberExpenseCreator memberExpenseCreator;
	private final GroupMemberReader groupMemberReader;

	public ExpensesResponse createExpenses(Long meetId, ExpensesRequest request) {
		List<ExpenseResponse> expenses = request.expenses()
			.stream()
			.map(e -> createExpense(meetId, e))
			.toList();
		return new ExpensesResponse(expenses);
	}

	private ExpenseResponse createExpense(Long meetId, ExpenseRequest request) {
		Expense expense = expenseCreator.create(meetId, request);
		List<MemberExpenseResponse> memberExpensesResponses = request.memberExpenses().stream()
			.map(m -> {
				GroupMember groupMember = groupMemberReader.getByGroupMemberId(m.memberId());
				return MemberExpenseResponse.of(memberExpenseCreator.create(expense, groupMember, m));
			}).toList();

		return ExpenseResponse.of(expense, memberExpensesResponses);
	}

	public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request) {
		Expense expense = expenseUpdater.update(expenseId, request);
		return ExpenseResponse.of(expense);

	}

	public void deleteExpense(Long expenseId) {
		//TODO 삭제하는 사람이 정산자인지 확인 로직 필요
		expenseDeleter.delete(expenseId);
	}
}
