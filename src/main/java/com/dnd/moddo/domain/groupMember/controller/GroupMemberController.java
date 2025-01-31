package com.dnd.moddo.domain.groupMember.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
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
		@RequestParam("meetId") String token, //아마 토큰으로 받고 모임 Id
		@Valid @RequestBody GroupMembersSaveRequest request
	) {
		Long meetId = 1L; //mock value
		GroupMembersResponse response = commandGroupMemberService.createGroupMembers(meetId, request);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<GroupMembersResponse> getGroupMembers(
		@RequestParam("meetId") String token
	) {
		Long meetId = 1L;
		GroupMembersResponse response = queryGroupMemberService.findAll(meetId);
		return ResponseEntity.ok(response);
	}
}
