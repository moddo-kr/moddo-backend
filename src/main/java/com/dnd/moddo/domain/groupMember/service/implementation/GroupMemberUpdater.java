package com.dnd.moddo.domain.groupMember.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class GroupMemberUpdater {
	private final GroupMemberRepository groupMemberRepository;
	private final GroupMemberValidator groupMemberValidator;

}
