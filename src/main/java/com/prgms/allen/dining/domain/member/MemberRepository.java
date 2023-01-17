package com.prgms.allen.dining.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgms.allen.dining.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	@Query("select m from Member m where m.id = :memberId and m.memberType = 'CUSTOMER'")
	Optional<Member> findCustomerById(@Param("memberId") Long memberId);
}
