package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMembersSaveRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GroupMemberCreator {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberValidator groupMemberValidator;
	private final GroupReader groupReader;
	private final UserRepository userRepository;

	public List<GroupMember> create(Long groupId, Long userId, GroupMembersSaveRequest request) {
		Group group = groupReader.read(groupId);

		List<String> requestNames = request.extractNames();

		groupMemberValidator.validateMemberNamesNotDuplicate(requestNames);

		List<GroupMember> newMembers = new ArrayList<>();

		newMembers.add(createManager(userId, group));
		newMembers.addAll(request.toEntity(group));

		return groupMemberRepository.saveAll(newMembers);
	}

	private GroupMember createManager(Long userId, Group group) {
		User user = userRepository.getById(userId);
		String name = user.getIsMember() ? user.getName() : "김모또";
		return GroupMember.builder()
			.name(name)
			.profileId(null)     //user 프로필 가져오기
			.group(group)
			.isPaid(true)
			.role(ExpenseRole.MANAGER)
			.build();

	}
}
