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

		GroupMember newMember = request.toEntity(group, null, ExpenseRole.PARTICIPANT);
		newMember = groupMemberRepository.save(newMember);

		String profileUrl = getProfileUrl(newMember.getId());
		newMember.updateProfile(profileUrl);

		return newMember;
	}

	public GroupMember updatePaymentStatus(Long groupMemberId, PaymentStatusUpdateRequest request) {
		GroupMember groupMember = groupMemberRepository.getById(groupMemberId);
		groupMember.updatePaymentStatus(request.isPaid());
		return groupMember;
	}

	private String getProfileUrl(Long memberId) {
		if (memberId == null || memberId < 1) {
			return s3Bucket.getS3Url() + "profile/moddo.png";
		}

		Long finalId = (memberId - 1) % 9 + 1; // 🔥 ID를 1~9 범위로 변환
		return s3Bucket.getS3Url() + "profile/" + finalId + ".png";
	}
}
