package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupReader {
    private final GroupRepository groupRepository;

    public Group read(Long groupId) {
        return groupRepository.getById(groupId);
    }

}
