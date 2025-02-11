package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.user.entity.User;
import com.dnd.moddo.domain.user.repository.UserRepository;
import com.dnd.moddo.global.jwt.dto.GroupTokenResponse;
import com.dnd.moddo.global.jwt.utill.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupCreator {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public GroupTokenResponse createGroup(GroupRequest request, Long userId) {
        User user = userRepository.getById(userId);
        String encryptedPassword = passwordEncoder.encode(request.password());

        Group group = Group.builder()
                .writer(user.getId())
                .name(request.name())
                .password(encryptedPassword)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();

        groupRepository.save(group);

        return jwtProvider.generateGroupToken(group.getWriter(), group.getId());
    }
}
