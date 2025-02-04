package com.dnd.moddo.domain.groupMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberNotFoundException;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

	List<GroupMember> findByMeetId(Long meetId);

	default GroupMember getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new GroupMemberNotFoundException(id));
	}
}
