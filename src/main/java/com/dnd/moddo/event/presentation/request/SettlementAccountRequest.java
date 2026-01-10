package com.dnd.moddo.event.presentation.request;

public record SettlementAccountRequest(
	String bank,

	String accountNumber
) {
}
