package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GroupMemberReader {
	private final GroupMemberRepository groupMemberRepository;

	public List<GroupMember> getAllByGroupId(Long groupId) {
		return groupMemberRepository.findByGroupId(groupId);
	}

	public GroupMember getByGroupMemberId(Long groupMemberId) {
		return groupMemberRepository.getById(groupMemberId);
	}

	public List<Long> findIdsByGroupId(Long groupId) {
		return groupMemberRepository.findGroupMemberIdsByGroupId(groupId);
	}

}
