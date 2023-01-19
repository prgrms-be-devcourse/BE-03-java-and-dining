package com.prgms.allen.dining.global.error.exception;

import com.prgms.allen.dining.global.error.ErrorCode;

public class IllegalReservationStateException extends IllegalStateException {

	private final ErrorCode errorCode;

	public IllegalReservationStateException(String message) {
		this(ErrorCode.ILLEGAL_MODIFICATION, message);
	}

	public IllegalReservationStateException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
