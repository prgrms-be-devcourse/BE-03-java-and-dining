package com.prgms.allen.dining.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.security.jwt.JwtAuthenticationProvider;
import com.prgms.allen.dining.security.jwt.JwtProvider;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	static final String LOGIN_REQUEST_URL = "/api/members/login";
	static final String SIGNUP_REQUEST_URL = "/api/members/signup";
	static final String OWNER_API_URL_PREFIX = "/owner/api/**";
	static final String CUSTOMER_API_URL_PREFIX = "/customer/api/**";
	static final String CUSTOMER_RESTAURANT_API_URL_PREFIX = "/customer/api/restaurants/**";
	static final String RESERVATION_AVAILABLE_TIMES_API_URL_PREFIX = "/customer/api/reservations/available-times";
	static final String[] ANONYMOUS_AND_CUSTOMER_API_URL_PREFIX = {
		CUSTOMER_RESTAURANT_API_URL_PREFIX,
		RESERVATION_AVAILABLE_TIMES_API_URL_PREFIX
	};

	private final MemberService memberService;
	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailureHandler loginFailureHandler;
	private final JwtProvider jwtProvider;

	public WebSecurityConfig(
		MemberService memberService,
		LoginSuccessHandler loginSuccessHandler,
		LoginFailureHandler loginFailureHandler,
		JwtProvider jwtProvider
	) {
		this.memberService = memberService;
		this.loginSuccessHandler = loginSuccessHandler;
		this.loginFailureHandler = loginFailureHandler;
		this.jwtProvider = jwtProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			.csrf().disable()
			.headers().disable()
			.formLogin().disable()
			.rememberMe().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http
			.addFilterBefore(
				jwtAuthenticationFilter(),
				UsernamePasswordAuthenticationFilter.class
			)
			.addFilterBefore(
				jsonUsernamePasswordAuthenticationFilter(authenticationManager()),
				UsernamePasswordAuthenticationFilter.class
			);

		http
			.authorizeRequests()
			.antMatchers(LOGIN_REQUEST_URL, SIGNUP_REQUEST_URL)
			.permitAll()
			.antMatchers(OWNER_API_URL_PREFIX)
			.hasRole(MemberType.OWNER.toString())
			.antMatchers(ANONYMOUS_AND_CUSTOMER_API_URL_PREFIX)
			.access("hasRole('" + MemberType.CUSTOMER + "') or isAnonymous()")
			.antMatchers(CUSTOMER_API_URL_PREFIX)
			.hasAnyRole(MemberType.CUSTOMER.toString());

		return http.build();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtProvider);
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
		return new ProviderManager(jwtAuthenticationProvider());
	}

	@Bean
	public JwtAuthenticationProvider jwtAuthenticationProvider() {
		return new JwtAuthenticationProvider(memberService, jwtProvider);
	}
}
