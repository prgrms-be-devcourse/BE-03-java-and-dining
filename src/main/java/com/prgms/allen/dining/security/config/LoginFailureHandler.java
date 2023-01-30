package com.prgms.allen.dining.security.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgms.allen.dining.global.error.ErrorCode;
import com.prgms.allen.dining.global.error.ErrorResponse;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginFailureHandler.class);

	private final ObjectMapper objectMapper;

	public LoginFailureHandler() {
		this.objectMapper = new ObjectMapper();
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {
		log.info("Login failed.", exception);

		response.setContentType(HeaderValue.CONTENT_TYPE.getValue());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_REQUEST);
		response.setStatus(errorResponse.httpStatus().value());
		response.getWriter().write(
			objectMapper.writeValueAsString(errorResponse)
		);
	}
}
