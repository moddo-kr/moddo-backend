package com.dnd.moddo.domain.groupMember.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.groupMember.dto.response.GroupMembersResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberReader;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryGroupMemberService {
	private final GroupMemberReader groupMemberReader;

	public GroupMembersResponse findAll(Long meetId) {
		List<GroupMember> members = groupMemberReader.getAll(meetId);
		return GroupMembersResponse.of(members);
	}
}
