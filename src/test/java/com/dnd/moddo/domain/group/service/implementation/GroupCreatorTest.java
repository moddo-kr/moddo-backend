package com.dnd.moddo.domain.group.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import com.dnd.moddo.domain.user.entity.type.Authority;
import com.dnd.moddo.global.jwt.dto.GroupTokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GroupCreatorTest {
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private GroupCreator groupCreator;

    private Long userId;
    private GroupRequest request;
    private User mockUser;
    private String encodedPassword;
    private Group mockGroup;

    @BeforeEach
    void setUp() {
        userId = 1L;
        request = new GroupRequest("groupName", "password", LocalDateTime.now().plusDays(1));
        mockUser = new User(userId, "test@example.com", "닉네임", "프로필", false, LocalDateTime.now(), LocalDateTime.now().plusDays(1), Authority.USER);
        encodedPassword = "encryptedPassword";
        mockGroup = new Group(request.name(), userId, "password", LocalDateTime.now(), LocalDateTime.now().plusMonths(1), "groupName", encodedPassword, LocalDateTime.now().plusDays(1));
    }

    @DisplayName("사용자는 모임 생성에 성공한다.")
    @Test
    void createGroupSuccess() {
        // given
        when(userRepository.getById(userId)).thenReturn(mockUser);
        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassword);
        when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);
        when(jwtProvider.generateGroupToken(mockGroup.getId())).thenReturn(new GroupTokenResponse("group-token"));

        // when
        GroupTokenResponse response = groupCreator.createGroup(request, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.groupToken()).isEqualTo("group-token");

        verify(userRepository, times(1)).getById(userId);
        verify(passwordEncoder, times(1)).encode(request.password());
        verify(groupRepository, times(1)).save(any(Group.class));
        verify(jwtProvider, times(1)).generateGroupToken(mockGroup.getId());
    }
}
