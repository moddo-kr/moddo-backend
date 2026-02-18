package com.dnd.moddo.event.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.common.support.VerifyManagerPermission;
import com.dnd.moddo.event.application.command.CommandMemberService;
import com.dnd.moddo.event.application.query.QueryMemberService;
import com.dnd.moddo.event.application.query.QuerySettlementService;
import com.dnd.moddo.event.presentation.request.MemberSaveRequest;
import com.dnd.moddo.event.presentation.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.event.presentation.response.MemberResponse;
import com.dnd.moddo.event.presentation.response.MembersResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/group-members")
@RestController
public class MemberController {
	private final QueryMemberService queryMemberService;
	private final CommandMemberService commandMemberService;
	private final QuerySettlementService querySettlementService;

	@GetMapping
	public ResponseEntity<MembersResponse> getAppointmentMembers(
		@RequestParam("groupToken") String code
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		MembersResponse response = queryMemberService.findAll(settlementId);
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@PutMapping
	public ResponseEntity<MemberResponse> addAppointmentMember(
		@RequestParam("groupToken") String code,
		@Valid @RequestBody MemberSaveRequest request
	) {
		Long settlementId = querySettlementService.findIdByCode(code);
		MemberResponse response = commandMemberService.addAppointmentMember(settlementId,
			request);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{appointmentMemberId}/payment")
	public ResponseEntity<MemberResponse> updatePaymentStatus(
		@RequestParam("groupToken") String code,
		@PathVariable("appointmentMemberId") Long appointmentMemberId,
		@RequestBody PaymentStatusUpdateRequest request) {
		MemberResponse response = commandMemberService.updatePaymentStatus(appointmentMemberId,
			request);
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@DeleteMapping("/{appointmentMemberId}")
	public ResponseEntity<Void> deleteAppointmentMember(
		@PathVariable("appointmentMemberId") Long appointmentMemberId
	) {
		commandMemberService.delete(appointmentMemberId);
		return ResponseEntity.noContent().build();
	}

}
