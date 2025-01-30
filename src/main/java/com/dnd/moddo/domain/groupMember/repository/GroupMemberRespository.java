package com.dnd.moddo.domain.groupMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;

public interface GroupMemberRespository extends JpaRepository<GroupMember, Long> {

	List<GroupMember> findByMeetId(Long meetId);
}
