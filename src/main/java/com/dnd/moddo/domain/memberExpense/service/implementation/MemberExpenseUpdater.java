package com.dnd.moddo.domain.memberExpense.service.implementation;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;

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
