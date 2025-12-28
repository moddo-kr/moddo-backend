package com.dnd.moddo.event.application.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.infrastructure.MemberExpenseRepository;

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
