package com.prgms.allen.dining.global.error.exception;

import com.prgms.allen.dining.global.error.ErrorCode;

public class NotificationFailedException extends RuntimeException {

	private final ErrorCode errorCode;

	public NotificationFailedException(String message) {
		super(message);
		this.errorCode = ErrorCode.NOTIFICATION_CONNECTION_ERROR;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
