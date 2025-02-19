package com.dnd.moddo.domain.groupMember.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
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
	private final GroupReader groupReader;
	private final UserRepository userRepository;

	public GroupMember createManagerForGroup(Long userId, Long groupId) {
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
}
