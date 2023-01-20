package com.prgms.allen.dining.domain.member;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.dto.MemberSignupRequest;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.global.error.ErrorCode;
import com.prgms.allen.dining.global.error.exception.NotFoundResourceException;

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

	public Member findOwnerById(long ownerId) {
		return memberRepository.findByIdAndMemberType(ownerId, MemberType.OWNER)
			.orElseThrow(() -> new NotFoundResourceException(
				ErrorCode.NOT_FOUND_RESOURCE,
				MessageFormat.format("Cannot find Owner entity for owner id = {0}", ownerId)
			));
	}

	public Member findCustomerById(long customerId) {
		return memberRepository.findByIdAndMemberType(customerId, MemberType.CUSTOMER)
			.orElseThrow(() -> new NotFoundResourceException(
				ErrorCode.NOT_FOUND_RESOURCE,
				MessageFormat.format("Cannot find Customer entity for customer id = {0}", customerId)
			));
	}
}
