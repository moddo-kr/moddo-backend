package com.dnd.moddo.domain.image.exception;

import com.dnd.moddo.global.exception.ModdoException;
import org.springframework.http.HttpStatus;

public class CharacterNotFoundException extends ModdoException {
    public CharacterNotFoundException() { super(HttpStatus.NOT_FOUND, "해당 캐릭터를 찾을 수 없습니다."); }
}
