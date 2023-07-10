package com.prgms.allen.dining.domain.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByIdAndMemberType(Long id, MemberType memberType);

	Optional<Member> findByNickname(String nickname);

	// @Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select m from Member m where m.id = :id and m.memberType = :memberType")
	Optional<Member> findCustomerForReserve(
		@Param(value = "id") Long id,
		@Param(value = "memberType") MemberType memberType
	);

}
