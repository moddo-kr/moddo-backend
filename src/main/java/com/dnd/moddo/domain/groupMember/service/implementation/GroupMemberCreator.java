package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GroupMemberCreator {
	private final GroupMemberRepository groupMemberRepository;

	@Transactional
	public List<GroupMember> createGroupMember(Long meetId, GroupMembersSaveRequest request) {
		List<GroupMember> groupMembers = request.toEntity(meetId);
		return groupMemberRepository.saveAll(groupMembers);
	}
}
