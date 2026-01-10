package com.dnd.moddo.domain.image.exception;

import org.springframework.http.HttpStatus;

import com.dnd.moddo.common.exception.ModdoException;

public class S3SaveException extends ModdoException {
	public S3SaveException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "사진을 저장하던 중 에러가 발생했습니다.");
	}
}
