package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.group.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.group.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.group.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.group.service.implementation.GroupValidator;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.global.common.annotation.VerifyManagerPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryGroupService {
    private final GroupReader groupReader;
    private final GroupValidator groupValidator;

    public GroupDetailResponse findOne(Long groupId, Long userId) {
        Group group = groupReader.read(groupId);
        groupValidator.checkGroupAuthor(group, userId);
        List<GroupMember> members = groupReader.findByGroup(groupId);
        return GroupDetailResponse.of(group, members);
    }

    public GroupHeaderResponse findByGroupHeader(Long groupId) {
        return groupReader.findByHeader(groupId);
    }
}
