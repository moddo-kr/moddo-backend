package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberUpdater {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberReader groupMemberReader;
	private final GroupMemberValidator groupMemberValidator;
	private final GroupReader groupReader;
	private final UserRepository userRepository;

	public GroupMember addToGroup(Long groupId, GroupMemberSaveRequest request) {
		Group group = groupReader.read(groupId);
		List<GroupMember> groupMembers = groupMemberReader.findAllByGroupId(groupId);

		List<String> existingNames = new ArrayList<>(groupMembers.stream().map(GroupMember::getName).toList());
		existingNames.add(request.name());

		groupMemberValidator.validateMemberNamesNotDuplicate(existingNames);

		return groupMemberRepository.save(request.toEntity(group, ExpenseRole.PARTICIPANT));
	}

	public GroupMember addManagerToGroup(Long userId, Long groupId) {
		User user = userRepository.getById(userId);
		Group group = groupReader.read(groupId);

		String name = user.getIsMember() ? user.getName() : "김모또";

		GroupMember groupMember = GroupMember.builder()
			.name(name)
			.profileId(null)     //user 프로필 가져오기
			.group(group)
			.role(ExpenseRole.MANAGER)
			.build();

		groupMember.updatePaymentStatus(true);

		return groupMemberRepository.save(groupMember);
	}

	public GroupMember updatePaymentStatus(Long groupMemberId, PaymentStatusUpdateRequest request) {
		GroupMember groupMember = groupMemberRepository.getById(groupMemberId);
		groupMember.updatePaymentStatus(request.isPaid());
		return groupMember;
	}
}
