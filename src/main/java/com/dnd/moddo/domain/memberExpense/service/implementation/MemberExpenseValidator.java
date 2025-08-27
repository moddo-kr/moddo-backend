package com.dnd.moddo.domain.memberExpense.service.implementation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dnd.moddo.domain.appointmentMember.exception.InvalidAppointmentMemberException;
import com.dnd.moddo.domain.appointmentMember.service.implementation.AppointmentMemberReader;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemberExpenseValidator {
	private final AppointmentMemberReader appointmentMemberReader;

	public void validateMembersArePartOfGroup(Long groupId, List<MemberExpenseRequest> requests) {
		Set<Long> validGroupMemberIds = new HashSet<>(appointmentMemberReader.findIdsByGroupId(groupId));
		List<Long> requestedGroupMemberIds = requests.stream()
			.map(MemberExpenseRequest::id)
			.toList();

		requestedGroupMemberIds.forEach(id -> {
			if (!validGroupMemberIds.contains(id)) {
				throw new InvalidAppointmentMemberException(id);
			}
		});

	}
}
