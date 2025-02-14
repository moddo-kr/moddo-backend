package com.dnd.moddo.domain.memberExpense.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberExpenseDeleter {

	private final MemberExpenseRepository memberExpenseRepository;

	public void deleteAllByExpenseId(Long expenseId) {
		List<MemberExpense> memberExpenses = memberExpenseRepository.findByExpenseId(expenseId);
		memberExpenseRepository.deleteAll(memberExpenses);
	}

	public void deleteByMemberExpense(MemberExpense memberExpense) {
		memberExpenseRepository.delete(memberExpense);
	}

	public void deleteByMemberExpenses(List<MemberExpense> memberExpenses) {
		memberExpenseRepository.deleteAll(memberExpenses);
	}
}
