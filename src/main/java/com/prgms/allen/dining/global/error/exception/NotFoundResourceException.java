package com.prgms.allen.dining.global.error.exception;

import com.prgms.allen.dining.global.error.ErrorCode;

public class NotFoundResourceException extends IllegalArgumentException {

	private final ErrorCode errorCode;

	public NotFoundResourceException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
