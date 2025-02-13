package com.dnd.moddo.domain.groupMember.service.implementation;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.exception.ManagerCannotDeleteException;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberDeleter {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberReader groupMemberReader;

	public void delete(Long groupMemberId) {
		GroupMember groupMember = groupMemberReader.findByGroupMemberId(groupMemberId);
		if (groupMember.isManager()) {
			throw new ManagerCannotDeleteException(groupMemberId);
		}
		groupMemberRepository.delete(groupMember);
	}
}
