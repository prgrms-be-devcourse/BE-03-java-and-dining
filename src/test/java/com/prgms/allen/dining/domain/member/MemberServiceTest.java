package com.prgms.allen.dining.domain.member;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prgms.allen.dining.domain.member.dto.MemberSignupReq;
import com.prgms.allen.dining.domain.member.entity.MemberType;

class MemberServiceTest {

	private final MemberRepository memberRepository = new FakeMemberRepository();
	private final MemberService memberService = new MemberService(memberRepository);

	@AfterEach
	void tearDown() {
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("사용자는 회원가입 할 수 있다.")
	public void signup() {
		// given
		final MemberSignupReq memberSignupReq = new MemberSignupReq("닉네임", "이택승", "01012341234",
			"qwer1234!", MemberType.CUSTOMER);

		// when
		memberService.signup(memberSignupReq);

		// then
		final long count = memberRepository.count();
		assertThat(count)
			.isEqualTo(1L);
	}
}