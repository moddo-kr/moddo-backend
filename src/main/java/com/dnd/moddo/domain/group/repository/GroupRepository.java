package com.dnd.moddo.domain.group.repository;

import com.dnd.moddo.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
