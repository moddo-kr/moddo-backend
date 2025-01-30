package com.dnd.moddo.domain.groupMember.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRespository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class GroupMemberReader {
	private final GroupMemberRespository groupMemberRespository;

	public List<GroupMember> getAll(Long meetId) {
		return groupMemberRespository.findByMeetId(meetId);
	}

}
