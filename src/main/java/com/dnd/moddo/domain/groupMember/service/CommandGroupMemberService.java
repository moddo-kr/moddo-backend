package com.dnd.moddo.domain.groupMember.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandGroupMemberService {
	private final GroupMemberCreator groupMemberCreator;
	private final GroupRepository groupRepository; //추후 groupReader나 다른것으로 수정할 예정

	public GroupMembersResponse createGroupMembers(Long groupId, GroupMembersSaveRequest request) {
		List<GroupMember> members = groupMemberCreator.createGroupMember(groupId, request);
		return GroupMembersResponse.of(members);
	}

}
