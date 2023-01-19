package com.prgms.allen.dining.global.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.prgms.allen.dining.global.error.exception.IllegalReservationStateException;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;
import com.prgms.allen.dining.global.error.exception.RestaurantDuplicateCreationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		log.info("RuntimeException occurred.", e);
		ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
		return newResponseEntity(response);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
		log.info("IllegalStateException occurred.", e);
		ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_PARAMETER);
		return newResponseEntity(response);
	}

	@ExceptionHandler(NotFoundResourceException.class)
	public ResponseEntity<ErrorResponse> handleNotFoundResourceException(NotFoundResourceException e) {
		log.info("NotFoundResourceException occurred.", e);
		ErrorResponse response = new ErrorResponse(e.getErrorCode());
		return newResponseEntity(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.info("MethodArgumentNotValidException occurred.", e);
		ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_PARAMETER);
		return newResponseEntity(response);
	}

	@ExceptionHandler(RestaurantDuplicateCreationException.class)
	public ResponseEntity<ErrorResponse> handleRestaurantDuplicateCreationException(
		RestaurantDuplicateCreationException e) {
		log.info("RestaurantDuplicateCreationException occurred.", e);
		ErrorResponse response = new ErrorResponse(e.getErrorCode());
		return newResponseEntity(response);
	}

	@ExceptionHandler(IllegalReservationStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalReservationStateException(IllegalReservationStateException e) {
		log.info("IllegalReservationStateException occurred.", e);
		ErrorResponse response = new ErrorResponse(e.getErrorCode());
		return newResponseEntity(response);
	}

	private ResponseEntity<ErrorResponse> newResponseEntity(ErrorResponse response) {
		return ResponseEntity.status(response.httpStatus())
			.body(response);
	}
}