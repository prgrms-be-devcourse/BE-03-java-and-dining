package com.prgms.allen.dining.global.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		log.info("RuntimeException occurred.", e);
		ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
		return newResponseEntity(response);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handelIllegalArgumentException(IllegalArgumentException e) {
		log.info("IllegalArgumentException occurred.", e);
		ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_PARAMETER);
		return newResponseEntity(response);
	}

	private ResponseEntity<ErrorResponse> newResponseEntity(ErrorResponse response) {
		return ResponseEntity.status(response.httpStatus())
			.body(response);
	}
}