package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

public record SettlementListResponse(
	Long groupId,
	String groupCode,
	String name,
	Long totalAmount,
	Long totalMemberCount,
	Long completedMemberCount,
	LocalDateTime createdAt,
	LocalDateTime completedAt

) {
}
