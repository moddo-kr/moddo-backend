package com.dnd.moddo.domain.memberExpense.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.memberExpense.dto.response.MemberExpenseResponse;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.service.implementation.MemberExpenseReader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryMemberExpenseService {
	private final MemberExpenseReader memberExpenseReader;

	public List<MemberExpenseResponse> findAllByExpenseId(Long expenseId) {
		List<MemberExpense> memberExpenses = memberExpenseReader.findAllByExpenseId(expenseId);
		return memberExpenses.stream()
			.map(MemberExpenseResponse::of)
			.toList();
	}
}
