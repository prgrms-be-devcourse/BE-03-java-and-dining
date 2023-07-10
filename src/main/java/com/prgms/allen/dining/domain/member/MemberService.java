package com.prgms.allen.dining.domain.member;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.common.NotFoundResourceException;
import com.prgms.allen.dining.domain.member.dto.MemberSignupReq;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;

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

	public Member findOwnerById(Long ownerId) {
		return memberRepository.findByIdAndMemberType(ownerId, MemberType.OWNER)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Owner entity for owner id = {0}", ownerId)
			));
	}

	public Member findCustomerById(Long customerId) {
		return memberRepository.findByIdAndMemberType(customerId, MemberType.CUSTOMER)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Customer entity for customer id = {0}", customerId)
			));
	}

	public Member login(String nickname, String password) {
		Member member = memberRepository.findByNickname(nickname)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Member entity for member nickname = {0}", nickname)
			));
		member.checkPassword(password);
		return member;
	}

	public Member findCustomerForReserve(Long customerId) {
		return memberRepository.findCustomerForReserve(customerId, MemberType.CUSTOMER)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("Cannot find Customer entity for customer id = {0}", customerId)
			));
	}
}
