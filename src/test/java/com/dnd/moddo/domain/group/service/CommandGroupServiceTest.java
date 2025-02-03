package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.GroupRequest;
import com.dnd.moddo.domain.group.dto.GroupResponse;
import com.dnd.moddo.domain.group.service.implementation.GroupCreater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandGroupServiceTest {

    @Mock
    private GroupCreater groupCreater;

    @InjectMocks
    private CommandGroupService commandGroupService;

    private GroupRequest groupRequest;
    private GroupResponse groupResponse;

    @BeforeEach
    void setUp() {
        groupRequest = new GroupRequest("GroupName", "password123", LocalDateTime.now(), "bank", "1234-1234");
        groupResponse = new GroupResponse(1L, 1L, LocalDateTime.now(), LocalDateTime.now().minusDays(1), "bank", "1234-1234");
    }

    @Test
    void createGroup() {
        // Given
        when(groupCreater.createGroup(any(GroupRequest.class), anyLong())).thenReturn(groupResponse);

        // When
        GroupResponse response = commandGroupService.createGroup(groupRequest, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.writer()).isEqualTo(1L);
        assertThat(response.bank()).isEqualTo("bank");
        assertThat(response.accountNumber()).isEqualTo("1234-1234");
    }
}