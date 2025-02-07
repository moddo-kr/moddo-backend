package com.dnd.moddo.domain.groupMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberNotFoundException;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

	List<GroupMember> findByGroupId(Long groupId);

	@Query("select m.id from GroupMember m where m.group.id = :groupId")
	List<Long> findGroupMemberIdsByGroupId(@Param("groupId") Long groupId);

	default GroupMember getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new GroupMemberNotFoundException(id));
	}
}
