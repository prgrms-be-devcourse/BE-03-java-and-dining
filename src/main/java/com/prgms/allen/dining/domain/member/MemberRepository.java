package com.prgms.allen.dining.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
