package com.dnd.moddo.domain.group.service.implementation;

import com.dnd.moddo.domain.group.exception.GroupNotAuthorException;
import org.springframework.stereotype.Service;

@Service
public class GroupValidator {
    public void checkGroupAuthor(Long groupWriter, Long writer) {
        if (!groupWriter.equals(writer)) {
            throw new GroupNotAuthorException();
        }
    }
}
