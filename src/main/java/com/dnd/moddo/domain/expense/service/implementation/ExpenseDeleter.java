package com.dnd.moddo.domain.expense.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpenseDeleter {

	private final ExpenseRepository expenseRepository;

	public void delete(Long expenseId) {
		Expense expense = expenseRepository.getById(expenseId);
		expenseRepository.delete(expense);
	}
}
