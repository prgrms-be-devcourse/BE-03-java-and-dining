package com.prgms.allen.dining.domain.member;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.dto.MemberSignupRequest;
import com.prgms.allen.dining.domain.member.entity.Member;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public void signup(MemberSignupRequest signupRequest) {
		final Member newMember = signupRequest.toEntity();
		memberRepository.save(newMember);
	}

	public Optional<Member> findCustomerById(Long memberId) {
		return memberRepository.findCustomerById(memberId);
	}
}
