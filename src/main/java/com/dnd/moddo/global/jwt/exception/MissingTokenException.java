package com.dnd.moddo.global.jwt.exception;

import com.dnd.moddo.global.exception.ModdoException;
import org.springframework.http.HttpStatus;

public class MissingTokenException extends ModdoException {
    public MissingTokenException() {
        super(HttpStatus.UNAUTHORIZED, "토큰이 없습니다.");
    }
}
