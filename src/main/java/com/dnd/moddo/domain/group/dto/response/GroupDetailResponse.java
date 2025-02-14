package com.dnd.moddo.domain.group.dto.response;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.dto.response.GroupMemberResponse;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;

import java.util.List;
import java.util.stream.Collectors;

public record GroupDetailResponse(
        Long id,
        String groupName,
        List<GroupMemberResponse> members
) {
    public static GroupDetailResponse of(Group group, List<GroupMember> members) {
        List<GroupMemberResponse> memberResponses = members.stream()
                .map(GroupMemberResponse::of)
                .collect(Collectors.toList());
        return new GroupDetailResponse(
                group.getId(),
                group.getName(),
                memberResponses
        );
    }
}
