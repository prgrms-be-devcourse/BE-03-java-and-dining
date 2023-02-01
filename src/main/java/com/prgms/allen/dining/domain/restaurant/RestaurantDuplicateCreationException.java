package com.prgms.allen.dining.domain.restaurant;

import com.prgms.allen.dining.global.error.ErrorCode;

public class RestaurantDuplicateCreationException extends RuntimeException {

	private final ErrorCode errorCode;

	public RestaurantDuplicateCreationException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public RestaurantDuplicateCreationException(String message) {
		super(message);
		this.errorCode = ErrorCode.DUPLICATE_ERROR;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
}
