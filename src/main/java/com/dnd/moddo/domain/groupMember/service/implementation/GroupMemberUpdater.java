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
import com.dnd.moddo.global.config.S3Bucket;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberUpdater {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberReader groupMemberReader;
	private final GroupMemberValidator groupMemberValidator;
	private final GroupReader groupReader;
	private final S3Bucket s3Bucket;

	public GroupMember addToGroup(Long groupId, GroupMemberSaveRequest request) {
		Group group = groupReader.read(groupId);
		List<GroupMember> groupMembers = groupMemberReader.findAllByGroupId(groupId);

		List<String> existingNames = new ArrayList<>(groupMembers.stream().map(GroupMember::getName).toList());
		existingNames.add(request.name());

		groupMemberValidator.validateMemberNamesNotDuplicate(existingNames);

		List<GroupMember> membersOnly = groupMembers.stream()
			.filter(member -> !member.isManager())
			.toList();

		List<Integer> usedProfiles = membersOnly.stream()
			.map(GroupMember::getProfileId)
			.toList();

		int newProfileId = findAvailableProfileId(usedProfiles);

		GroupMember newMember = request.toEntity(group, newProfileId, null, ExpenseRole.PARTICIPANT);
		newMember = groupMemberRepository.save(newMember);

		String profileUrl = getProfileUrl(newProfileId);
		newMember.updateProfile(profileUrl);

		return newMember;
	}

	public GroupMember updatePaymentStatus(Long groupMemberId, PaymentStatusUpdateRequest request) {
		GroupMember groupMember = groupMemberRepository.getById(groupMemberId);
		groupMember.updatePaymentStatus(request.isPaid());
		return groupMember;
	}

	private int findAvailableProfileId(List<Integer> usedProfiles) {
		for (int i = 1; i <= 8; i++) {
			if (!usedProfiles.contains(i)) {
				return i;
			}
		}

		int maxProfileId = usedProfiles.stream().max(Integer::compareTo).orElse(0);
		return (maxProfileId % 8) + 1;
	}

	private String getProfileUrl(int profileId) {
		return s3Bucket.getS3Url() + "profile/" + profileId + ".png";
	}
}
