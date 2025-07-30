package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.dto.response.GroupSaveResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupCreator;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.group.service.implementation.GroupUpdater;
import com.dnd.moddo.domain.group.service.implementation.GroupValidator;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.service.CommandGroupMemberService;
import com.dnd.moddo.global.jwt.utill.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandGroupService {
	private final GroupCreator groupCreator;
	private final GroupUpdater groupUpdater;
	private final GroupValidator groupValidator;
	private final GroupReader groupReader;
	private final JwtProvider jwtProvider;
	private final CommandGroupMemberService commandGroupMemberService;
	private final GroupRepository groupRepository;

	public GroupSaveResponse createGroup(GroupRequest request, Long userId) {
		Group group = groupCreator.createGroup(request, userId);
		GroupMemberResponse manager = commandGroupMemberService.createManager(group, userId);
		return new GroupSaveResponse(group.getCode(), manager);
	}

	public GroupResponse updateAccount(GroupAccountRequest request, Long userId, Long groupId) {
		Group group = groupReader.read(groupId);
		groupValidator.checkGroupAuthor(group, userId);
		group = groupUpdater.updateAccount(request, group.getId());
		return GroupResponse.of(group);
	}

	public GroupPasswordResponse isPasswordMatch(Long groupId, Long userId, GroupPasswordRequest request) {
		Group group = groupReader.read(groupId);
		groupValidator.checkGroupAuthor(group, userId);
		GroupPasswordResponse response = groupValidator.checkGroupPassword(request, group.getPassword());
		return response;
	}

	public Group read(Long groupId) {
		return groupRepository.getById(groupId);
	}
}
