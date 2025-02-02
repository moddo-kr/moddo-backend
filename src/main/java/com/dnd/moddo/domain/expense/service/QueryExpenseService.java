package com.dnd.moddo.domain.expense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.expense.dto.response.ExpenseResponse;
import com.dnd.moddo.domain.expense.dto.response.ExpensesResponse;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.service.implementation.ExpenseReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryExpenseService {
	private final ExpenseReader expenseReader;

	public ExpensesResponse findAllByMeetId(Long meetId) {
		List<Expense> expenses = expenseReader.findAllByMeetId(meetId);
		return ExpensesResponse.of(expenses);
	}

	public ExpenseResponse findOneByExpenseId(Long meetId, Long expenseId) {
		Expense expense = expenseReader.findOneByExpenseId(expenseId);
		return ExpenseResponse.of(expense);
	}
}
