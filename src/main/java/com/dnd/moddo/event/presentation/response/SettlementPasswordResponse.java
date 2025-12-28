package com.dnd.moddo.event.presentation.response;

public record SettlementPasswordResponse(
	String status
) {
	public static SettlementPasswordResponse from(String status) {
		return new SettlementPasswordResponse(status);
	}
}
