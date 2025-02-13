package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotAuthorException;
import org.springframework.stereotype.Service;

@Service
public class GroupValidator {
    public void checkGroupAuthor(Group group, Long userId) {
        if (!group.isWriter(userId)) {
            throw new GroupNotAuthorException();
        }
    }
}
