package com.prgms.allen.dining.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값을 확인해주세요."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다."),
	DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "중복 생성 할 수 없습니다."),
	NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
