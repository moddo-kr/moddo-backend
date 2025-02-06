package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GroupMemberUpdater {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberValidator groupMemberValidator;

	public List<GroupMember> updateGroupMember(Long groupId, GroupMembersSaveRequest request) {
		List<GroupMember> existingMembers = groupMemberRepository.findByGroupId(groupId);

		List<String> allNames = getAllGroupMemberName(existingMembers, request);

		groupMemberValidator.validateMemberNamesNotDuplicate(allNames);
		List<GroupMember> newMembers = request.toEntity(groupId);
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
