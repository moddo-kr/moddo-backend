package com.dnd.moddo.domain.groupMember.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
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

	public GroupMembersResponse createGroupMembers(Long groupId, GroupMembersSaveRequest request) {
		List<GroupMember> members = groupMemberCreator.createGroupMember(groupId, request);
		return GroupMembersResponse.of(members);
	}
}
