package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

public record SettlementShareResponse(
	Long settlementId,
	String name,
	String groupCode,
	LocalDateTime createdAt,
	LocalDateTime completedAt
) {
}