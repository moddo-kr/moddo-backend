package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupCreator;
import com.dnd.moddo.domain.group.service.implementation.GroupUpdater;
import com.dnd.moddo.global.jwt.dto.GroupTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandGroupService {
    private final GroupCreator groupCreator;
    private final GroupUpdater groupUpdater;

    public GroupTokenResponse createGroup(GroupRequest request, Long userId) {
        return groupCreator.createGroup(request, userId);
    }

    public GroupResponse updateAccount(GroupAccountRequest request, Long groupId) {
        Group group = groupUpdater.updateAccount(request, groupId);
        return GroupResponse.of(group);
    }
}
