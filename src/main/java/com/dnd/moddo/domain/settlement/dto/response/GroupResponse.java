package com.dnd.moddo.domain.settlement.dto.response;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.settlement.entity.Settlement;

public record GroupResponse(
	Long id,
	Long writer,
	LocalDateTime createdAt,
	LocalDateTime expiredAt,
	String bank,
	String accountNumber,
	LocalDateTime deadline
) {
	public static GroupResponse of(Settlement settlement) {
		return new GroupResponse(
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
