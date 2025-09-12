package com.dnd.moddo.domain.settlement.dto.response;

public record SettlementPasswordResponse(
	String status
) {
	public static SettlementPasswordResponse from(String status) {
		return new SettlementPasswordResponse(status);
	}
}
