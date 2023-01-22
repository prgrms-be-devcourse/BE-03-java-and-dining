package com.prgms.allen.dining.global.error.exception;

import com.prgms.allen.dining.global.error.ErrorCode;

public class NotFoundResourceException extends IllegalArgumentException {

	private final ErrorCode errorCode;

	public NotFoundResourceException(String message) {
		this(ErrorCode.NOT_FOUND_RESOURCE, message);
	}

	public NotFoundResourceException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
