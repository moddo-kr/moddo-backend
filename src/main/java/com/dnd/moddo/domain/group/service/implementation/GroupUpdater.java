package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupUpdater {
    private final GroupRepository groupRepository;

    public Group updateAccount(GroupAccountRequest request, Long groupId) {
        Group group = groupRepository.getById(groupId);
        group.updateAccount(request);
        return group;
    }
}
