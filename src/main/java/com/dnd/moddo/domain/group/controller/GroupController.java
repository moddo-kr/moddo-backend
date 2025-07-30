package com.dnd.moddo.domain.group.controller;

import com.dnd.moddo.domain.group.entity.Group;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.group.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.group.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.dto.response.GroupSaveResponse;
import com.dnd.moddo.domain.group.service.CommandGroupService;
import com.dnd.moddo.domain.group.service.QueryGroupService;
import com.dnd.moddo.global.jwt.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupController {
	private final CommandGroupService commandGroupService;
	private final JwtService jwtService;
	private final QueryGroupService queryGroupService;

	@PostMapping
	public ResponseEntity<GroupSaveResponse> saveGroup(HttpServletRequest request,
		@RequestBody GroupRequest groupRequest) {
		Long userId = jwtService.getUserId(request);
		GroupSaveResponse response = commandGroupService.createGroup(groupRequest, userId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/account")
	public ResponseEntity<GroupResponse> updateAccount(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@RequestBody GroupAccountRequest groupAccountRequest) {
		Long userId = jwtService.getUserId(request);
		Long groupId = queryGroupService.findIdByCode(code);

		GroupResponse response = commandGroupService.updateAccount(groupAccountRequest, userId, groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<GroupDetailResponse> getGroup(
		HttpServletRequest request,
		@RequestParam("groupToken") String code) {
		Long userId = jwtService.getUserId(request);
		Long groupId = queryGroupService.findIdByCode(code);

		GroupDetailResponse response = queryGroupService.findOne(groupId, userId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/password")
	public ResponseEntity<GroupPasswordResponse> isPasswordMatch(
		HttpServletRequest request,
		@RequestParam("groupToken") String code,
		@RequestBody GroupPasswordRequest groupPasswordRequest) {
		Long userId = jwtService.getUserId(request);
		Long groupId = queryGroupService.findIdByCode(code);

		GroupPasswordResponse response = commandGroupService.isPasswordMatch(groupId, userId, groupPasswordRequest);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/header")
	public ResponseEntity<GroupHeaderResponse> getHeader(
		@RequestParam("groupToken") String code) {
		Long groupId = queryGroupService.findIdByCode(code);

		GroupHeaderResponse response = queryGroupService.findByGroupHeader(groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/header/no-cache")
	public ResponseEntity<GroupHeaderResponse> getHeaderNoCache(
		@RequestParam("groupToken") String code) {
		Long groupId = queryGroupService.findIdByCodeNoCache(code);

		GroupHeaderResponse response = queryGroupService.findByGroupHeader(groupId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/get")
	public ResponseEntity<Group> groupGet(
			@RequestParam("groupToken") String code) {
		Long groupId = queryGroupService.findIdByCode(code);
		Group response = commandGroupService.read(groupId);
		return ResponseEntity.ok(response);
	}
}
