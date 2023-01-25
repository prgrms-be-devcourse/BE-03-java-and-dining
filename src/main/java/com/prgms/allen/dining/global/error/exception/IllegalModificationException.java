package com.prgms.allen.dining.global.error.exception;

import com.prgms.allen.dining.global.error.ErrorCode;

public class IllegalModificationException extends IllegalStateException {

	private final ErrorCode errorCode;

	public IllegalModificationException(String message) {
		this(ErrorCode.ILLEGAL_MODIFICATION, message);
	}

	public IllegalModificationException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
