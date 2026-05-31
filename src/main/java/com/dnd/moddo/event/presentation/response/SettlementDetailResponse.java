package com.dnd.moddo.event.presentation.response;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dnd.moddo.event.domain.member.Member;
import com.dnd.moddo.event.domain.settlement.Settlement;

public record SettlementDetailResponse(
	Long id,
	String groupName,
	List<SettlementMemberResponse> members
) {
	public static SettlementDetailResponse of(Settlement settlement, List<Member> members) {
		return of(settlement, members, Map.of());
	}

	public static SettlementDetailResponse of(
		Settlement settlement,
		List<Member> members,
		Map<Long, PaymentRequestSummaryResponse> paymentRequestByMemberId
	) {
		List<SettlementMemberResponse> memberResponses = members.stream()
			.map(member -> SettlementMemberResponse.of(member, paymentRequestByMemberId.get(member.getId())))
			.collect(Collectors.toList());
		return new SettlementDetailResponse(
			settlement.getId(),
			settlement.getName(),
			memberResponses
		);
	}
}
