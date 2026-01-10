package com.dnd.moddo.event.application.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dnd.moddo.event.domain.member.exception.InvalidMemberException;
import com.dnd.moddo.event.presentation.request.MemberExpenseRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MemberExpenseValidator {
	private final MemberReader memberReader;

	public void validateMembersArePartOfSettlement(Long settlementId, List<MemberExpenseRequest> requests) {
		Set<Long> validAppointmentMemberIds = new HashSet<>(
			memberReader.findIdsBySettlementId(settlementId));
		List<Long> requestedAppointmentMemberIds = requests.stream()
			.map(MemberExpenseRequest::id)
			.toList();

		requestedAppointmentMemberIds.forEach(id -> {
			if (!validAppointmentMemberIds.contains(id)) {
				throw new InvalidMemberException(id);
			}
		});

	}
}
