package com.prgms.allen.dining.domain.member;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgms.allen.dining.domain.member.dto.UserDetailsImpl;
import com.prgms.allen.dining.domain.member.entity.Member;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

	private final MemberRepository memberRepository;

	public UserDetailsServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
		return memberRepository.findByNickname(nickname)
			.map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException(
					String.format("Username(field nickname for our service) [%s] not found.", nickname)
				)
			);
	}

	private UserDetails createUserDetails(Member member) {
		return new UserDetailsImpl(
			member.getNickname(),
			member.getPassword(),
			Collections.singleton(
				new SimpleGrantedAuthority(member.getMemberType().getValue())
			)
		);
	}
}
