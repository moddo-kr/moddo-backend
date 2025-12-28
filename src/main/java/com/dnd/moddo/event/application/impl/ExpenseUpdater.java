package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.expense.Expense;
import com.dnd.moddo.event.infrastructure.ExpenseRepository;
import com.dnd.moddo.event.presentation.request.ExpenseImageRequest;
import com.dnd.moddo.event.presentation.request.ExpenseRequest;

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

	public Expense updateImgUrl(Long expenseId, ExpenseImageRequest request) {
		Expense expense = expenseRepository.getById(expenseId);
		expense.updateImgUrl(request.images());
		return expense;
	}
}
