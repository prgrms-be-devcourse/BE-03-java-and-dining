package com.prgms.allen.dining.web.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.prgms.allen.dining.web.security.handler.LoginFailureHandler;
import com.prgms.allen.dining.web.security.handler.LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	static final String LOGIN_REQUEST_URL = "/members/api/login";

	private final UserDetailsService userDetailsService;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;

	public WebSecurityConfig(
		UserDetailsService userDetailsService,
		LoginSuccessHandler loginSuccessHandler,
		LoginFailureHandler loginFailureHandler
	) {
		this.userDetailsService = userDetailsService;
		this.loginSuccessHandler = loginSuccessHandler;
		this.loginFailureHandler = loginFailureHandler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			.csrf().disable()
			.formLogin().disable();

		http
			.authorizeHttpRequests()
			.antMatchers(LOGIN_REQUEST_URL).permitAll()
			.and()
			.addFilterBefore(
				jsonUsernamePasswordAuthenticationFilter(authenticationManager()),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter(
		AuthenticationManager authenticationManager
	) {
		return new JsonUsernamePasswordAuthenticationFilter(
			authenticationManager,
			loginSuccessHandler,
			loginFailureHandler
		);
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(
			new AuthenticationProviderImpl(userDetailsService)
		);
	}
}
