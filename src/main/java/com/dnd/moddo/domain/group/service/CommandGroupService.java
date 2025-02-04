package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.GroupRequest;
import com.dnd.moddo.domain.group.dto.GroupResponse;
import com.dnd.moddo.domain.group.service.implementation.GroupCreater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommandGroupService {
    private final GroupCreater groupCreater;

    public GroupResponse createGroup(GroupRequest request, Long userId) {
        return groupCreater.createGroup(request, userId);
    }
}
