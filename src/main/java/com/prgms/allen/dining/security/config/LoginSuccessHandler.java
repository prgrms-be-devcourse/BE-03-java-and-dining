package com.prgms.allen.dining.security.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.prgms.allen.dining.security.jwt.JwtProvider;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginSuccessHandler.class);

	private final JwtProvider jwtProvider;

	public LoginSuccessHandler(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		log.info("Login Success. Authentication: {}", authentication);
		String jwtToken = jwtProvider.getToken(authentication);

		response.setContentType(HeaderValue.CONTENT_TYPE.getValue());
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		response.getWriter().write(jwtToken);
	}
}
