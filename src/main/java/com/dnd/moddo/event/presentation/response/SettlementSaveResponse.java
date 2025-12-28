package com.dnd.moddo.event.presentation.response;

public record SettlementSaveResponse(
	String groupToken,
	MemberResponse manager
) {
}
