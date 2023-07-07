package com.prgms.allen.dining.domain.fake;

import com.prgms.allen.dining.domain.member.entity.Member;

public class FakeMember extends Member {

	private Long id;

	public FakeMember(Member member, Long id) {
		super(member.getId(),
			member.getNickname(),
			member.getName(),
			member.getPhone(),
			member.getPassword(),
			member.getMemberType());
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
