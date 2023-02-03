package com.prgms.allen.dining.security.jwt;

import org.springframework.util.Assert;

public record JwtAuthenticationPrincipal(
	String jwtToken,
	Long memberId
) {

	public JwtAuthenticationPrincipal {
		Assert.hasLength(jwtToken, "jwtToken must not blank");
		Assert.notNull(memberId, "memberId must not blank");
	}

	@Override
	public String toString() {
		return "JwtAuthenticationPrincipal{" +
			"jwtToken(head 6 chars)='" + jwtToken.substring(0, 6) + '\'' +
			", memberId='" + memberId + '\'' +
			'}';
	}
}
