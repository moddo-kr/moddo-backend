package com.dnd.moddo.domain.expense.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.expense.dto.request.ExpenseRequest;
import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseValidator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpenseCreator {
	private final ExpenseRepository expenseRepository;
	private final MemberExpenseValidator memberExpenseValidator;
	private final GroupRepository groupRepository;

	public Expense create(Long groupId, int maxOrder, ExpenseRequest request) {
		Group group = groupRepository.getById(groupId);

		memberExpenseValidator.validateMembersArePartOfGroup(groupId, request.memberExpenses());

		Expense expense = request.toEntity(group, maxOrder);
		return expenseRepository.save(expense);
	}

}
