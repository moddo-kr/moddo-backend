package com.dnd.moddo.domain.group.repository;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    default Group getById(Long id) {
        return findById(id).orElseThrow(() -> new GroupNotFoundException(id));
    }
}
