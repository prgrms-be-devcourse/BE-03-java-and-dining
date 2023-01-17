package com.prgms.allen.dining.domain.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.prgms.allen.dining.domain.member.entity.Member;

public class FakeMemberRepository implements MemberRepository {

	private final List<Member> members = new ArrayList<>();

	@Override
	public List<Member> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Member> findAll(Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<Member> findAll(Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Member> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		return members.size();
	}

	@Override
	public void deleteById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Member entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Member> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		members.clear();
	}

	@Override
	public <S extends Member> S save(S entity) {
		Member newMember = new Member(
			count(),
			entity.getNickname(),
			entity.getName(),
			entity.getPhone(),
			entity.getPassword(),
			entity.getMemberType()
		);
		members.add(newMember);
		return (S)newMember;
	}

	@Override
	public <S extends Member> List<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Member> findById(Long aLong) {
		return members.stream()
			.filter(member -> Objects.equals(member.getId(), aLong))
			.findFirst();
	}

	@Override
	public boolean existsById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> S saveAndFlush(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> List<S> saveAllAndFlush(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch(Iterable<Member> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Member getOne(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Member getById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Member getReferenceById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> long count(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Member, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException();
	}
}
