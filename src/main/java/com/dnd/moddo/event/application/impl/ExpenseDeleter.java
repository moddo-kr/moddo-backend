package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpenseDeleter {

	private final ExpenseRepository expenseRepository;

	public void delete(Expense expense) {
		expenseRepository.delete(expense);
	}
}
