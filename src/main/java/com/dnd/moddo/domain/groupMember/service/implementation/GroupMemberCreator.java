package com.dnd.moddo.domain.groupMember.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.config.S3Bucket;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GroupMemberCreator {
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;
	private final S3Bucket s3Bucket;

	public GroupMember createManagerForGroup(Group group, Long userId) {
		User user = userRepository.getById(userId);

		String name = user.getIsMember() ? user.getName() : "김모또";

		GroupMember groupMember = GroupMember.builder()
			.name(name)
			.group(group)
			.profileId(0)
			.role(ExpenseRole.MANAGER)
			.build();

		groupMember.updatePaymentStatus(true);
		String profile = s3Bucket.getS3Url() + "profile/moddo.png";
		groupMember.updateProfile(profile);

		return groupMemberRepository.save(groupMember);
	}
}
