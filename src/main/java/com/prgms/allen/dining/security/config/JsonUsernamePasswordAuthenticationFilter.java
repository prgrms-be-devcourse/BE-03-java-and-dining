package com.prgms.allen.dining.security.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgms.allen.dining.security.jwt.JwtAuthenticationToken;

public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final String HTTP_METHOD = "POST";
	private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
		new AntPathRequestMatcher(WebSecurityConfig.LOGIN_REQUEST_URL, HTTP_METHOD);

	private final ObjectMapper objectMapper;

	public JsonUsernamePasswordAuthenticationFilter(
		AuthenticationManager authenticationManager,
		LoginSuccessHandler loginSuccessHandler,
		LoginFailureHandler loginFailureHandler
	) {
		super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);

		this.objectMapper = new ObjectMapper();
		setAuthenticationManager(authenticationManager);
		setAuthenticationSuccessHandler(loginSuccessHandler);
		setAuthenticationFailureHandler(loginFailureHandler);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		IOException {
		if (request.getContentType() == null || !request.getContentType().equals(HeaderValue.CONTENT_TYPE.getValue())) {
			throw new AuthenticationServiceException(
				String.format("Authentication Content-Type not supported: %s", request.getContentType())
			);
		}

		MemberLoginReq memberLoginReq = objectMapper.readValue(
			StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8),
			MemberLoginReq.class
		);
		String nickname = memberLoginReq.nickname();
		String password = memberLoginReq.password();

		if (nickname == null || password == null) {
			throw new AuthenticationServiceException(
				String.format("Invalid request body. [nickname=%s], [password=%s]", nickname, password)
			);
		}

		JwtAuthenticationToken unauthenticatedToken = JwtAuthenticationToken.unauthenticated(
			nickname,
			password
		);
		return this.getAuthenticationManager().authenticate(unauthenticatedToken);
	}
}
