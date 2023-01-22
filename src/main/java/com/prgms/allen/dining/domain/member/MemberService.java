package com.prgms.allen.dining.domain.member;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.dto.MemberSignupReq;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

@Service
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public Long signup(MemberSignupReq signupReq) {
		Member savedMember = memberRepository.save(signupReq.toEntity());
		return savedMember.getId();
	}

	public Member findOwnerById(long ownerId) {
		return memberRepository.findByIdAndMemberType(ownerId, MemberType.OWNER)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Restaurant entity for owner id = {0}", ownerId)
			));
	}

	public Member findCustomerById(Long customerId) {
		return memberRepository.findByIdAndMemberType(customerId, MemberType.CUSTOMER)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Restaurant entity for customer id = {0}", customerId)
			));
	}
}
