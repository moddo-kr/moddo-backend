package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupReader {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public Group read(Long groupId) {
        return groupRepository.getById(groupId);
    }

    public List<GroupMember> findByGroup(Group group) {
        return groupMemberRepository.findByGroupId(group.getId());
    }
}
