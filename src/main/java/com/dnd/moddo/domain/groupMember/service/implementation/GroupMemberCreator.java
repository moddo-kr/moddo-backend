package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GroupMemberCreator {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberValidator groupMemberValidator;
	private final GroupReader groupReader;

	public List<GroupMember> create(Long groupId, GroupMembersSaveRequest request) {
		Group group = groupReader.read(groupId);

		List<String> requestNames = request.members().stream().map(GroupMemberSaveRequest::name).toList();

		groupMemberValidator.validateManagerExists(request.members());
		groupMemberValidator.validateMemberNamesNotDuplicate(requestNames);

		List<GroupMember> newMembers = request.toEntity(group);
		return groupMemberRepository.saveAll(newMembers);
	}
}
