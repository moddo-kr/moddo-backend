package com.dnd.moddo.domain.memberExpense.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dnd.moddo.domain.expense.exception.InvalidGroupMemberException;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemberExpenseValidator {
	private final GroupMemberReader groupMemberReader;

	public void validateMembersArePartOfGroup(Long groupId, List<MemberExpenseRequest> requests) {
		Set<Long> validGroupMemberIds = new HashSet<>(groupMemberReader.findIdsByGroupId(groupId));
		List<Long> requestedGroupMemberIds = requests.stream()
			.map(MemberExpenseRequest::memberId)
			.toList();

		requestedGroupMemberIds.forEach(id -> {
			if (!validGroupMemberIds.contains(id)) {
				throw new InvalidGroupMemberException(id);
			}
		});

	}
}
