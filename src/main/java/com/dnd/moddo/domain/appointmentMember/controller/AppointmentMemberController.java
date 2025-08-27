package com.dnd.moddo.domain.appointmentMember.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.appointmentMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.appointmentMember.dto.request.appointmentMemberSaveRequest;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMemberResponse;
import com.dnd.moddo.domain.appointmentMember.dto.response.AppointmentMembersResponse;
import com.dnd.moddo.domain.appointmentMember.service.CommandAppointmentMemberService;
import com.dnd.moddo.domain.appointmentMember.service.QueryAppointmentMemberService;
import com.dnd.moddo.domain.settlement.service.QuerySettlementService;
import com.dnd.moddo.global.common.annotation.VerifyManagerPermission;
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/group-members")
@RestController
public class AppointmentMemberController {
	private final QueryAppointmentMemberService queryAppointmentMemberService;
	private final CommandAppointmentMemberService commandAppointmentMemberService;
	private final JwtService jwtService;
	private final QuerySettlementService querySettlementService;

	@GetMapping
	public ResponseEntity<AppointmentMembersResponse> getAppointmentMembers(
		@RequestParam("groupToken") String code
	) {
		Long groupId = querySettlementService.findIdByCode(code);
		AppointmentMembersResponse response = queryAppointmentMemberService.findAll(groupId);
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@PutMapping
	public ResponseEntity<AppointmentMemberResponse> addAppointmentMember(
		@RequestParam("groupToken") String code,
		@Valid @RequestBody appointmentMemberSaveRequest request
	) {
		Long groupId = querySettlementService.findIdByCode(code);
		AppointmentMemberResponse response = commandAppointmentMemberService.addAppointmentMember(groupId, request);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{appointmentMemberId}/payment")
	public ResponseEntity<AppointmentMemberResponse> updatePaymentStatus(
		@RequestParam("groupToken") String code,
		@PathVariable("appointmentMemberId") Long appointmentMemberId,
		@RequestBody PaymentStatusUpdateRequest request) {
		AppointmentMemberResponse response = commandAppointmentMemberService.updatePaymentStatus(appointmentMemberId,
			request);
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@DeleteMapping("/{appointmentMemberId}")
	public ResponseEntity<Void> deleteAppointmentMember(
		@PathVariable("appointmentMemberId") Long appointmentMemberId
	) {
		commandAppointmentMemberService.delete(appointmentMemberId);
		return ResponseEntity.noContent().build();
	}

}
