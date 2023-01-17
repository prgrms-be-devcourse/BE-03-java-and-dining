package com.prgms.allen.dining.web.domain.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prgms.allen.dining.domain.member.MemberService;
import com.prgms.allen.dining.domain.member.dto.MemberSignupRequest;

@RestController
@RequestMapping("/api/members")
public class MemberApi {

	private final MemberService memberService;

	public MemberApi(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(MemberSignupRequest memberSignupRequest) {
		memberService.signup(memberSignupRequest);
		return ResponseEntity.status(HttpStatus.CREATED)
			.build();
	}
}
