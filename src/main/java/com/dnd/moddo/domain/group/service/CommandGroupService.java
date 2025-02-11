package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupCreator;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.group.service.implementation.GroupUpdater;
import com.dnd.moddo.domain.group.service.implementation.GroupValidator;
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
    private final GroupValidator groupValidator;
    private final GroupReader groupReader;

    public GroupTokenResponse createGroup(GroupRequest request, Long userId) {
        return groupCreator.createGroup(request, userId);
    }

    public GroupResponse updateAccount(GroupAccountRequest request, Long userId, Long groupId) {
        Group group = groupReader.read(groupId);
        groupValidator.checkGroupAuthor(group.getWriter(), userId);
        group = groupUpdater.updateAccount(request, group.getId());
        return GroupResponse.of(group);
    }
}
