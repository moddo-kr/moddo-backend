package com.dnd.moddo.event.application.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.memberExpense.MemberExpense;
import com.dnd.moddo.event.infrastructure.MemberExpenseRepository;
import com.dnd.moddo.event.presentation.request.MemberExpenseRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberExpenseCreator {
	private final MemberExpenseRepository memberExpenseRepository;

	public MemberExpense create(Long expenseId, Member member,
		MemberExpenseRequest memberExpenseRequest) {
		MemberExpense memberExpense = memberExpenseRequest.toEntity(expenseId, member);
		return memberExpenseRepository.save(memberExpense);
	}
}
