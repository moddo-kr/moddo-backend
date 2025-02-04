package com.dnd.moddo.domain.expense.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.expense.dto.request.ExpensesRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpenseCreator {
	private final ExpenseRepository expenseRepository;

	public List<Expense> create(Long meetId, ExpensesRequest expensesRequest) {
		List<Expense> expenses = expensesRequest.toEntity(meetId);
		//TODO 멤버지출내역 저장하는 부분 추가
		return expenseRepository.saveAll(expenses);
	}
}
