package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

import com.dnd.moddo.event.domain.settlement.Settlement;

public record SettlementResponse(
	Long id,
	Long writer,
	LocalDateTime createdAt,
	LocalDateTime expiredAt,
	String bank,
	String accountNumber,
	LocalDateTime deadline
) {
	public static SettlementResponse of(Settlement settlement) {
		return new SettlementResponse(
			settlement.getId(),
			settlement.getWriter(),
			settlement.getCreatedAt(),
			settlement.getExpiredAt(),
			settlement.getBank(),
			settlement.getAccountNumber(),
			settlement.getDeadline()
		);
	}
}
