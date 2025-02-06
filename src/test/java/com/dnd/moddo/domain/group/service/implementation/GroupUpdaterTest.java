package com.dnd.moddo.domain.group.service.implementation;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotFoundException;
import com.dnd.moddo.domain.group.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
class GroupUpdaterTest {
    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private GroupUpdater groupUpdater;

    @DisplayName("그룹이 존재하면 계좌 정보를 수정할 수 있다.")
    @Test
    void updateAccountSuccess() {
        // given
        Long groupId = 1L;
        Group mockGroup = mock(Group.class);
        GroupAccountRequest request = mock(GroupAccountRequest.class);

        when(groupRepository.getById(eq(groupId))).thenReturn(mockGroup);

        // when
        Group updatedGroup = groupUpdater.updateAccount(request, groupId);

        // then
        verify(mockGroup, times(1)).updateAccount(any());
        assertThat(updatedGroup).isEqualTo(mockGroup);
    }

    @DisplayName("그룹이 존재하지 않으면 계좌 정보를 수정할 때 예외가 발생한다.")
    @Test
    void updateAccountNotFoundGroup() {
        // given
        Long groupId = 1L;
        GroupAccountRequest request = mock(GroupAccountRequest.class);

        doThrow(new GroupNotFoundException(groupId)).when(groupRepository).getById(groupId);

        // when & then
        assertThatThrownBy(() -> groupUpdater.updateAccount(request, groupId))
                .isInstanceOf(GroupNotFoundException.class);
    }

}
