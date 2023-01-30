package com.prgms.allen.dining.security.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class AuthenticationProviderImpl implements AuthenticationProvider {

	private final UserDetailsService userDetailsService;

	public AuthenticationProviderImpl(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)authentication;
		String nickname = token.getName();
		String password = (String)token.getCredentials();

		UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);

		if (!userDetails.getPassword().equals(password)) {
			throw new BadCredentialsException(
				String.format("Invalid password: %s", userDetails.getPassword()));
		}

		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
