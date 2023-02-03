package com.prgms.allen.dining.security.jwt;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.entity.Member;

public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final MemberService memberService;
	private final JwtProvider jwtProvider;

	public JwtAuthenticationProvider(MemberService memberService, JwtProvider jwtProvider) {
		this.memberService = memberService;
		this.jwtProvider = jwtProvider;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken)authentication;
		return doAuthenticate(
			String.valueOf(jwtAuthenticationToken.getPrincipal()),
			String.valueOf(jwtAuthenticationToken.getCredentials())
		);
	}

	private Authentication doAuthenticate(String nickname, String password) {
		try {
			Member member = memberService.login(nickname, password);
			List<GrantedAuthority> authorities = List.of(
				new SimpleGrantedAuthority(member.getMemberType().getValue())
			);

			String jwtToken = jwtProvider.generateToken(nickname, member.getId(), authorities);
			return JwtAuthenticationToken.authenticated(
				new JwtAuthenticationPrincipal(jwtToken, member.getId()),
				password,
				authorities
			);
		} catch (IllegalArgumentException e) {
			throw new BadCredentialsException(e.getMessage());
		} catch (Exception e) {
			throw new AuthenticationServiceException(e.getMessage());
		}
	}
}
