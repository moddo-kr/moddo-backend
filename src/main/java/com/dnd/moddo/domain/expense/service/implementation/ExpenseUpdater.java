package com.dnd.moddo.domain.expense.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.dto.request.ExpensesUpdateOrderRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpenseUpdater {
	private final ExpenseRepository expenseRepository;

	public Expense update(Long expenseId, ExpenseRequest request) {
		Expense expense = expenseRepository.getById(expenseId);
		expense.update(request.amount(), request.content(), request.date());
		return expense;
	}

	public List<Expense> updateOrder(ExpensesUpdateOrderRequest request) {
		return request.orders()
			.stream()
			.map(r -> {
				Expense expense = expenseRepository.getById(r.expenseId());
				expense.updateOrder(r.newOrder());
				return expense;
			}).toList();
	}
}
