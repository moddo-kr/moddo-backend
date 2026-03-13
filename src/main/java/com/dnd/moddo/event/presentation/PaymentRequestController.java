package com.dnd.moddo.event.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.auth.infrastructure.security.LoginUser;
import com.dnd.moddo.auth.presentation.response.LoginUserInfo;
import com.dnd.moddo.event.application.command.CommandPaymentRequest;
import com.dnd.moddo.event.application.query.QueryPaymentRequestService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.presentation.response.PaymentRequestResponse;
import com.dnd.moddo.event.presentation.response.PaymentRequestsResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PaymentRequestController {
	private final CommandPaymentRequest commandPaymentRequest;
	private final QueryPaymentRequestService queryPaymentRequestService;
	private final QuerySettlementService querySettlementService;

	@GetMapping("/payments")
	public ResponseEntity<PaymentRequestsResponse> getPaymentRequests(
		@LoginUser LoginUserInfo loginUser
	) {
		PaymentRequestsResponse response = queryPaymentRequestService.findByTargetUserId(loginUser.userId());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/groups/{code}/payments")
	public ResponseEntity<PaymentRequestResponse> createPaymentRequest(
		@PathVariable String code,
		@LoginUser LoginUserInfo loginUser
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		PaymentRequestResponse response = commandPaymentRequest.createPaymentRequest(settlementId, loginUser.userId());
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/payments/{paymentRequestId}/approve")
	public ResponseEntity<PaymentRequestResponse> approvePaymentRequest(
		@PathVariable Long paymentRequestId,
		@LoginUser LoginUserInfo loginUser
	) {
		PaymentRequestResponse response = commandPaymentRequest.approvePaymentRequest(paymentRequestId, loginUser.userId());
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/payments/{paymentRequestId}/reject")
	public ResponseEntity<PaymentRequestResponse> rejectPaymentRequest(
		@PathVariable Long paymentRequestId,
		@LoginUser LoginUserInfo loginUser
	) {
		PaymentRequestResponse response = commandPaymentRequest.rejectPaymentRequest(paymentRequestId, loginUser.userId());
		return ResponseEntity.ok(response);
	}
}
