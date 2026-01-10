package com.dnd.moddo.domain.image.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class CharacterNotFoundException extends ModdoException {
	public CharacterNotFoundException() {
		super(HttpStatus.NOT_FOUND, "캐릭터를 찾을 수 없습니다.");
	}
}
