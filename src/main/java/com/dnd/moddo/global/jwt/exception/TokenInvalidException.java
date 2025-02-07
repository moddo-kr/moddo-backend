package com.dnd.moddo.global.jwt.exception;

import com.dnd.moddo.global.exception.ModdoException;
import org.springframework.http.HttpStatus;

public class TokenInvalidException extends ModdoException {
    public TokenInvalidException() {
        super(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다.");
    }
}
