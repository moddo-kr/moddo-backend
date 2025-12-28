package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;

import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.infrastructure.MemberExpenseRepository;
import com.dnd.moddo.event.presentation.request.MemberExpenseRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberExpenseUpdater {
	private final MemberExpenseRepository memberExpenseRepository;

	public void update(MemberExpense memberExpense, MemberExpenseRequest request) {
		memberExpense.updateAmount(request.amount());
		memberExpenseRepository.save(memberExpense);
	}
}
