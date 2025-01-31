package com.dnd.moddo.domain.groupMember.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberUpdator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandGroupMemberService {
	private final GroupMemberCreator groupMemberCreator;
	private final GroupMemberUpdator groupMemberUpdator;

	public GroupMembersResponse createGroupMembers(Long meetId, GroupMembersSaveRequest request) {
		List<GroupMember> members = groupMemberCreator.createGroupMember(meetId, request);
		return GroupMembersResponse.of(members);
	}
}
