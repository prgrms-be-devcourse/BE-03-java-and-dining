package com.prgms.allen.dining.generator;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.security.jwt.JwtProvider;

@Component
public class JwtGenerator {

	private static final String BEARER_PREFIX = "Bearer";
	private static final String DELIMITER = " ";

	private final JwtProvider jwtProvider;

	public JwtGenerator(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	public String getToken(Member dummyMember) {
		return BEARER_PREFIX + DELIMITER
			+ jwtProvider.generateToken(
			dummyMember.getNickname(),
			dummyMember.getId(),
			List.of(
				new SimpleGrantedAuthority(dummyMember.getMemberType().getValue())
			)
		);
	}
}
