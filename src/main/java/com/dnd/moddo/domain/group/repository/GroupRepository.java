package com.dnd.moddo.domain.group.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotFoundException;

public interface GroupRepository extends JpaRepository<Group, Long> {
	default Group getById(Long groupId) {
		return findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
	}

	boolean existsByCode(String code);

	@Query("SELECT g.id FROM Group g WHERE g.code = :code")
	Optional<Long> findIdByCode(@Param("code") String code);

	default Long getIdByCode(String code) {
		return findIdByCode(code).orElseThrow(() -> new IllegalArgumentException("code notFound"));
	}

}
