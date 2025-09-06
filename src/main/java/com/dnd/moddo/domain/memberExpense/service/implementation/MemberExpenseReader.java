package com.dnd.moddo.domain.memberExpense.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberExpenseReader {
	private final MemberExpenseRepository memberExpenseRepository;

	public List<MemberExpense> findAllByExpenseId(Long expenseId) {
		return memberExpenseRepository.findByExpenseId(expenseId);
	}

	public List<MemberExpense> findAllByAppointMemberIds(List<Long> appointMemberIds) {
		return memberExpenseRepository.findAllByAppointmentMemberIds(appointMemberIds);
	}

	public List<MemberExpense> findAllByExpenseIds(List<Long> expenseIds) {
		return memberExpenseRepository.findAllByExpenseIds(expenseIds);
	}
}
