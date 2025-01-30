package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRespository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GroupMemberCreator {
	private final GroupMemberRespository groupMemberRespository;

	@Transactional
	public List<GroupMember> createGroupMember(Long meetId, GroupMembersSaveRequest request) {
		List<GroupMember> groupMembers = request.toEntity(meetId);
		return groupMemberRespository.saveAll(groupMembers);
	}
}
