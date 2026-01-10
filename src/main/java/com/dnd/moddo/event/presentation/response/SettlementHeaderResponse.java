package com.dnd.moddo.event.presentation.response;

import java.time.LocalDateTime;

public record SettlementHeaderResponse(
	String groupName,
	Long totalAmount,
	LocalDateTime deadline,
	String bank,
	String accountNumber
) {
	public static SettlementHeaderResponse of(String groupName, Long totalAmount, LocalDateTime deadline, String bank,
		String accountNumber) {
		return new SettlementHeaderResponse(groupName, totalAmount, deadline, bank, accountNumber);
	}
}
