package com.dnd.moddo.domain.groupMember.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberUpdater;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandGroupMemberService {
	private final GroupMemberCreator groupMemberCreator;
	private final GroupMemberUpdater groupMemberUpdater;

	public GroupMembersResponse create(Long groupId, GroupMembersSaveRequest request) {
		List<GroupMember> members = groupMemberCreator.create(groupId, request);
		return GroupMembersResponse.of(members);
	}

	public GroupMemberResponse addGroupMember(Long groupId, GroupMemberSaveRequest request) {
		GroupMember groupMember = groupMemberUpdater.addToGroup(groupId, request);
		return GroupMemberResponse.of(groupMember);
	}

}
