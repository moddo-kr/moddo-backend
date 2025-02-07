package com.dnd.moddo.domain.groupMember.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.service.CommandGroupMemberService;
import com.dnd.moddo.domain.groupMember.service.QueryGroupMemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/group-members")
@RestController
public class GroupMemberController {
	private final QueryGroupMemberService queryGroupMemberService;
	private final CommandGroupMemberService commandGroupMemberService;

	@PostMapping
	public ResponseEntity<GroupMembersResponse> saveGroupMembers(
		@RequestParam("groupId") Long groupId,
		@Valid @RequestBody GroupMembersSaveRequest request
	) {
		GroupMembersResponse response = commandGroupMemberService.createGroupMembers(groupId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<GroupMembersResponse> getGroupMembers(
		@RequestParam("groupId") Long groupId
	) {
		GroupMembersResponse response = queryGroupMemberService.findAll(groupId);
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<GroupMemberResponse> updateGroupMember(
		@RequestParam("groupId") Long groupId,
		@Valid @RequestBody GroupMemberSaveRequest request
	) {
		GroupMemberResponse response = commandGroupMemberService.addGroupMember(groupId, request);
		return ResponseEntity.ok(response);
	}

}
