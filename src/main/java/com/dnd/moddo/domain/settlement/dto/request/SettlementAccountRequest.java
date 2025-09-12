package com.dnd.moddo.domain.settlement.dto.request;

public record SettlementAccountRequest(
	String bank,

	String accountNumber
) {
}
