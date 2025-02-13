package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberUpdater {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberReader groupMemberReader;
	private final GroupMemberValidator groupMemberValidator;
	private final GroupRepository groupRepository;

	public GroupMember addToGroup(Long groupId, GroupMemberSaveRequest request) {
		Group group = groupRepository.getById(groupId);
		List<GroupMember> groupMembers = groupMemberReader.findAllByGroupId(groupId);

		List<String> existingNames = new ArrayList<>(groupMembers.stream().map(GroupMember::getName).toList());
		existingNames.add(request.name());

		groupMemberValidator.validateMemberNamesNotDuplicate(existingNames);

		return groupMemberRepository.save(request.toEntity(group, ExpenseRole.PARTICIPANT));
	}
}
