package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.group.service.implementation.GroupValidator;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryGroupService {
    private final GroupReader groupReader;
    private final GroupValidator groupValidator;

    public GroupDetailResponse findOne(Long groupId, Long userId) {
        Group group = groupReader.read(groupId);
        groupValidator.checkGroupAuthor(group, userId);
        List<GroupMember> members = groupReader.findByGroup(group);
        return GroupDetailResponse.of(group, members);
    }
}
