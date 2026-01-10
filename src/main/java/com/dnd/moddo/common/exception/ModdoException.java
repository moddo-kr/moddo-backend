package com.dnd.moddo.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModdoException extends RuntimeException {
	private final HttpStatus status;
	private final String message;
}
