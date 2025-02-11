package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupReaderTest {

    @Mock
    private GroupRepository groupRepository;

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
}
