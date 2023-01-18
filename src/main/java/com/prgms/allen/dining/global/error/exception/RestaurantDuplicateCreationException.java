package com.prgms.allen.dining.global.error.exception;

import com.prgms.allen.dining.global.error.ErrorCode;

public class RestaurantDuplicateCreationException extends RuntimeException {

	private final ErrorCode errorCode;

	public RestaurantDuplicateCreationException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
}
