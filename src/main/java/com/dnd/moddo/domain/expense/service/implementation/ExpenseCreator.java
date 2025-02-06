package com.dnd.moddo.domain.expense.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpenseCreator {
	private final ExpenseRepository expenseRepository;

	public Expense create(Long groupId, ExpenseRequest request) {
		Expense expense = request.toEntity(groupId);
		return expenseRepository.save(expense);
	}
}
