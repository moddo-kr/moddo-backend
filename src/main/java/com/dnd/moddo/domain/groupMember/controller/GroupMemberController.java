package com.dnd.moddo.domain.groupMember.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.group.service.QueryGroupService;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.service.CommandGroupMemberService;
import com.dnd.moddo.domain.groupMember.service.QueryGroupMemberService;
import com.dnd.moddo.global.common.annotation.VerifyManagerPermission;
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/group-members")
@RestController
public class GroupMemberController {
	private final QueryGroupMemberService queryGroupMemberService;
	private final CommandGroupMemberService commandGroupMemberService;
	private final JwtService jwtService;
	private final QueryGroupService queryGroupService;

	@GetMapping
	public ResponseEntity<GroupMembersResponse> getGroupMembers(
		@RequestParam("coce") String code
	) {
		Long groupId = queryGroupService.findIdByCode(code);
		GroupMembersResponse response = queryGroupMemberService.findAll(groupId);
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@PutMapping
	public ResponseEntity<GroupMemberResponse> addGroupMember(
		@RequestParam("code") String code,
		@Valid @RequestBody GroupMemberSaveRequest request
	) {
		Long groupId = queryGroupService.findIdByCode(code);
		GroupMemberResponse response = commandGroupMemberService.addGroupMember(groupId, request);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{groupMemberId}/payment")
	public ResponseEntity<GroupMemberResponse> updatePaymentStatus(
		@RequestParam("code") String code,
		@PathVariable("groupMemberId") Long groupMemberId,
		@RequestBody PaymentStatusUpdateRequest request) {
		GroupMemberResponse response = commandGroupMemberService.updatePaymentStatus(groupMemberId, request);
		return ResponseEntity.ok(response);
	}

	@VerifyManagerPermission
	@DeleteMapping("/{groupMemberId}")
	public ResponseEntity<Void> deleteGroupMember(
		@PathVariable("groupMemberId") Long groupMemberId
	) {
		commandGroupMemberService.delete(groupMemberId);
		return ResponseEntity.noContent().build();
	}

}
