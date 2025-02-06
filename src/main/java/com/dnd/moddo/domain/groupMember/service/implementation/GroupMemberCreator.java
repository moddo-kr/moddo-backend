package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
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
	private final GroupRepository groupRepository; //추후 grouopReader나 다른것으로 수정할 예정

	public List<GroupMember> createGroupMember(Long groupId, GroupMembersSaveRequest request) {
		Group group = groupRepository.getById(groupId);

		List<GroupMember> existingMembers = groupMemberRepository.findByGroupId(groupId);
		List<String> allNames = getAllGroupMemberName(existingMembers, request);

		groupMemberValidator.validateMemberNamesNotDuplicate(allNames);

		List<GroupMember> newMembers = request.toEntity(group);
		existingMembers.addAll(groupMemberRepository.saveAll(newMembers));

		return existingMembers;
	}

	private List<String> getAllGroupMemberName(List<GroupMember> existingMembers, GroupMembersSaveRequest request) {
		List<String> existingNames = existingMembers.stream().map(GroupMember::getName).toList();

		List<String> newNames = request.members().stream().map(GroupMemberSaveRequest::name).toList();

		List<String> allNames = new ArrayList<>(existingNames);
		allNames.addAll(newNames);
		return allNames;
	}
}
