package com.dnd.moddo.domain.settlement.dto.response;

import java.time.LocalDateTime;

public record GroupHeaderResponse(
	String groupName,
	Long totalAmount,
	LocalDateTime deadline,
	String bank,
	String accountNumber
) {
	public static GroupHeaderResponse of(String groupName, Long totalAmount, LocalDateTime deadline, String bank,
		String accountNumber) {
		return new GroupHeaderResponse(groupName, totalAmount, deadline, bank, accountNumber);
	}
}
