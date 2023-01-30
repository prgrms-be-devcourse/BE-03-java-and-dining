package com.prgms.allen.dining.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByIdAndMemberType(Long id, MemberType memberType);

	Optional<Member> findByNickname(String nickname);
}
