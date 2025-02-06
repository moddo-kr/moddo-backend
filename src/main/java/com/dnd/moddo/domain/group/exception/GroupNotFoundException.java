package com.dnd.moddo.domain.group.exception;

import com.dnd.moddo.global.exception.ModdoException;
import org.springframework.http.HttpStatus;

public class GroupNotFoundException extends ModdoException {
    public GroupNotFoundException(Long groupId){
        super(HttpStatus.NOT_FOUND, "아이디가 " + groupId + "인 모임을 찾을 수 없습니다.");
    };
}
