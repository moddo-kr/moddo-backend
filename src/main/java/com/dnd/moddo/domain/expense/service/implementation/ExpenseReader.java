package com.dnd.moddo.domain.expense.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ExpenseReader {
	private final ExpenseRepository expenseRepository;

	public List<Expense> findAllByGroupId(Long groupId) {
		return expenseRepository.findByGroupIdOrderByDateAsc(groupId);
	}

	public Expense findByExpenseId(Long expenseId) {
		return expenseRepository.getById(expenseId);
	}

}
