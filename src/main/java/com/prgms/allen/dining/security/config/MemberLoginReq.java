package com.prgms.allen.dining.security.config;

public record MemberLoginReq(
	String nickname,
	String password
) {
}
