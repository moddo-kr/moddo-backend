package com.dnd.moddo.domain.settlement.dto.response;

public record GroupPasswordResponse(
	String status
) {
	public static GroupPasswordResponse from(String status) {
		return new GroupPasswordResponse(status);
	}
}
