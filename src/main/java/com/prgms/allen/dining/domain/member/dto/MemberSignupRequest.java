package com.prgms.allen.dining.domain.member.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;

public record MemberSignupRequest(

	@NotBlank
	String nickname,

	@NotBlank
	String name,

	@NotBlank
	String phone,

	@NotBlank
	String password,

	@NotNull
	MemberType memberType
) {

	public Member toEntity() {
		return new Member(nickname, name, phone, password, memberType);
	}
}
