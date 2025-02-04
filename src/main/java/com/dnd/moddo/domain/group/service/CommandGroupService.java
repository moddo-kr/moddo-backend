package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.service.implementation.GroupCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommandGroupService {
    private final GroupCreator groupCreator;

    public GroupResponse createGroup(GroupRequest request, Long userId) {
        return groupCreator.createGroup(request, userId);
    }
}
