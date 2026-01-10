package com.dnd.moddo.event.presentation.response;

import java.util.List;
import java.util.stream.Collectors;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;

public record SettlementDetailResponse(
	Long id,
	String groupName,
	List<MemberResponse> members
) {
	public static SettlementDetailResponse of(Settlement settlement, List<Member> members) {
		List<MemberResponse> memberResponses = members.stream()
			.map(MemberResponse::of)
			.collect(Collectors.toList());
		return new SettlementDetailResponse(
			settlement.getId(),
			settlement.getName(),
			memberResponses
		);
	}
}
