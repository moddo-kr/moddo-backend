package com.dnd.moddo.domain.groupMember.service;

import org.springframework.stereotype.Service;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.dto.request.GroupMemberSaveRequest;
import com.dnd.moddo.domain.groupMember.dto.request.PaymentStatusUpdateRequest;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberCreator;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberDeleter;
import com.dnd.moddo.domain.groupMember.service.implementation.GroupMemberUpdater;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommandGroupMemberService {
	private final GroupMemberCreator groupMemberCreator;
	private final GroupMemberUpdater groupMemberUpdater;
	private final GroupMemberDeleter groupMemberDeleter;

	public GroupMemberResponse createManager(Group group, Long userId) {
		GroupMember groupMember = groupMemberCreator.createManagerForGroup(group, userId);
		return GroupMemberResponse.of(groupMember);
	}

	public GroupMemberResponse addGroupMember(Long groupId, GroupMemberSaveRequest request) {
		GroupMember groupMember = groupMemberUpdater.addToGroup(groupId, request);
		return GroupMemberResponse.of(groupMember);
	}

	public GroupMemberResponse updatePaymentStatus(Long groupMemberId, PaymentStatusUpdateRequest request) {
		GroupMember groupMember = groupMemberUpdater.updatePaymentStatus(groupMemberId, request);
		return GroupMemberResponse.of(groupMember);
	}

	public void delete(Long groupMemberId) {
		groupMemberDeleter.delete(groupMemberId);
	}

}
