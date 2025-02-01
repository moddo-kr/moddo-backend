package com.dnd.moddo.domain.user.exception;

import com.dnd.moddo.global.exception.ModdoException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ModdoException {
    public UserNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, email + "이 이메일인 유저가 없습니다.");
    }

    public UserNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, id + "인 아이디를 가진 유저가 없습니다.");
    }
}
