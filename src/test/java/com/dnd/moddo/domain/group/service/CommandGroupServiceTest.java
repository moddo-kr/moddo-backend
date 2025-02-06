package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupCreator;
import com.dnd.moddo.domain.group.service.implementation.GroupUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandGroupServiceTest {

    @Mock
    private GroupCreator groupCreator;

    @Mock
    private GroupUpdater groupUpdater; // 추가

    @InjectMocks
    private CommandGroupService commandGroupService;

    private GroupRequest groupRequest;
    private GroupResponse groupResponse;
    private GroupAccountRequest groupAccountRequest;
    private Group updatedGroup;

    @BeforeEach
    void setUp() {
        groupRequest = new GroupRequest("GroupName", "password123", LocalDateTime.now());
        groupResponse = new GroupResponse(1L, 1L, LocalDateTime.now(), LocalDateTime.now().minusDays(1), "bank", "1234-1234");
        groupAccountRequest = new GroupAccountRequest("newBank", "5678-5678");

        updatedGroup = mock(Group.class);
    }

    @Test
    @DisplayName("그룹을 생성할 수 있다.")
    void createGroup() {
        // Given
        when(groupCreator.createGroup(any(GroupRequest.class), anyLong())).thenReturn(groupResponse);

        // When
        GroupResponse response = commandGroupService.createGroup(groupRequest, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.writer()).isEqualTo(1L);
    }

    @Test
    @DisplayName("그룹의 계좌 정보를 업데이트할 수 있다.")
    void updateGroupAccount() {
        // Given
        when(groupUpdater.updateAccount(any(GroupAccountRequest.class), anyLong())).thenReturn(updatedGroup);

        // When
        GroupResponse result = commandGroupService.updateAccount(groupAccountRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        verify(groupUpdater, times(1)).updateAccount(any(GroupAccountRequest.class), anyLong()); // 호출 검증 추가
    }
}
