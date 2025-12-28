package com.dnd.moddo.event.presentation.request;

import static com.dnd.moddo.event.domain.member.ExpenseRole.*;

import java.util.List;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;

import jakarta.validation.Valid;

public record MembersSaveRequest(
	@Valid List<MemberSaveRequest> members
) {
	public List<Member> toEntity(Settlement settlement) {
		return members.stream()
			.map(m -> m.toEntity(settlement, null, PARTICIPANT))
			.toList();
	}

	public List<String> extractNames() {
		return members().stream().map(MemberSaveRequest::name).toList();
	}
}
