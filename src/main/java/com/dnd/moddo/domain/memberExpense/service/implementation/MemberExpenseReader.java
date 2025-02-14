package com.dnd.moddo.domain.memberExpense.service.implementation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public Map<Long, List<MemberExpense>> findAllByGroupMemberIds(List<Long> groupMemberIds) {
		return memberExpenseRepository.findAllByGroupMemberIds(groupMemberIds)
			.stream()
			.collect(Collectors.groupingBy(me -> me.getGroupMember().getId()));
	}
}
