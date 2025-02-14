package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupReaderTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @InjectMocks
    private GroupReader groupReader;

    @Test
    @DisplayName("그룹 ID를 통해 그룹을 정상적으로 조회할 수 있다.")
    void readGroup_Success() {
        // Given
        Long groupId = 1L;
        Group mockGroup = mock(Group.class);

        when(groupRepository.getById(anyLong())).thenReturn(mockGroup);

        // When
        Group result = groupReader.read(groupId);

        // Then
        assertThat(result).isNotNull();
        verify(groupRepository, times(1)).getById(groupId);
    }

    @Test
    @DisplayName("그룹을 통해 그룹 멤버 목록을 정상적으로 조회할 수 있다.")
    void findByGroup_Success() {
        // Given
        Group mockGroup = mock(Group.class);
        when(mockGroup.getId()).thenReturn(1L);
        List<GroupMember> mockMembers = List.of(mock(GroupMember.class), mock(GroupMember.class));

        when(groupMemberRepository.findByGroupId(anyLong())).thenReturn(mockMembers);

        // When
        List<GroupMember> result = groupReader.findByGroup(mockGroup.getId());

        // Then
        assertThat(result).hasSize(2);
        verify(groupMemberRepository, times(1)).findByGroupId(mockGroup.getId());
    }
}
