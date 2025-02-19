package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.group.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotAuthorException;
import com.dnd.moddo.domain.group.exception.InvalidPasswordException;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupValidator {

    private final PasswordEncoder passwordEncoder;

    private final GroupRepository groupRepository;

    public void checkGroupAuthor(Group group, Long userId) {
        if (!group.isWriter(userId)) {
            throw new GroupNotAuthorException();
        }
    }

    public GroupPasswordResponse checkGroupPassword(GroupPasswordRequest groupPasswordRequest, String getPassword) {
        boolean isMatch = passwordEncoder.matches(groupPasswordRequest.password(), getPassword);

        if (!isMatch) {
            throw new InvalidPasswordException();
        }

        return GroupPasswordResponse.from("확인되었습니다.");
    }
}
