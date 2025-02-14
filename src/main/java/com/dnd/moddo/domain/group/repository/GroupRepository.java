package com.dnd.moddo.domain.group.repository;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    default Group getById(Long groupId) {
        return findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
    }
}
