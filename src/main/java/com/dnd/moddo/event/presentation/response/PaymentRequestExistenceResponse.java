package com.dnd.moddo.event.presentation.response;

public record PaymentRequestExistenceResponse(
	boolean exists
) {
	public static PaymentRequestExistenceResponse of(boolean exists) {
		return new PaymentRequestExistenceResponse(exists);
	}
}
