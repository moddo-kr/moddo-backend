package com.dnd.moddo.domain.image.exception;

import com.dnd.moddo.global.exception.ModdoException;
import org.springframework.http.HttpStatus;

public class InvalidUniqueKeyException extends ModdoException {
    public InvalidUniqueKeyException() { super(HttpStatus.UNAUTHORIZED, "유효하지 않는 uniqueKey입니다.");}
}
