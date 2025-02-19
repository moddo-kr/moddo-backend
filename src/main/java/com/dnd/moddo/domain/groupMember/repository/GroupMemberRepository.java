package com.dnd.moddo.domain.groupMember.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.exception.GroupMemberNotFoundException;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

	@Query("select gm from GroupMember gm where gm.group.id = :groupId order by "
		+ "case when gm.role = 'MANAGER' then 1 else 2 end, "
		+ "case when gm.paidAt is null then 1 else 0 end, "
		+ "gm.paidAt asc, "
		+ "gm.name asc")
	List<GroupMember> findByGroupId(@Param("groupId") Long groupId);

	@Query("select gm.id from GroupMember gm where gm.group.id = :groupId")
	List<Long> findGroupMemberIdsByGroupId(@Param("groupId") Long groupId);

	default GroupMember getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new GroupMemberNotFoundException(id));
	}
}
