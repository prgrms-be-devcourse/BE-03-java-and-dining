package com.prgms.allen.dining.web.domain.member;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.dto.MemberSignupReq;

@RestController
@RequestMapping("/api/members")
public class MemberApi {

	private final MemberService memberService;

	public MemberApi(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(
		@Valid @RequestBody MemberSignupReq memberSignupReq
	) {
		memberService.signup(memberSignupReq);
		return ResponseEntity.status(HttpStatus.CREATED)
			.build();
	}
}
