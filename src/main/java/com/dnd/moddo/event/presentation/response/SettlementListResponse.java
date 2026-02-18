package com.dnd.moddo.event.presentation.response;

public record SettlementListResponse(
	Long groupId,
	String groupCode,
	String name,
	Long totalMemberCount,
	Long completedMemberCount

) {
}
