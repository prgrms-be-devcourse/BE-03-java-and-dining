package com.prgms.allen.dining.domain.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.prgms.allen.dining.domain.customer.entity.Customer;

public class FakeCustomerRepository implements CustomerRepository {

	private final List<Customer> customers = new ArrayList<>();

	@Override
	public List<Customer> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Customer> findAll(Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<Customer> findAll(Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Customer> findAllById(Iterable<Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		return customers.size();
	}

	@Override
	public void deleteById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Customer entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Customer> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		customers.clear();
	}

	@Override
	public <S extends Customer> S save(S entity) {
		customers.add(entity);
		return entity;
	}

	@Override
	public <S extends Customer> List<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Customer> findById(Long aLong) {
		throw new UnsupportedOperationException();
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
	public <S extends Customer> S saveAndFlush(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> List<S> saveAllAndFlush(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAllInBatch(Iterable<Customer> entities) {
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
	public Customer getOne(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Customer getById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Customer getReferenceById(Long aLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> Optional<S> findOne(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> List<S> findAll(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> List<S> findAll(Example<S> example, Sort sort) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> Page<S> findAll(Example<S> example, Pageable pageable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> long count(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer> boolean exists(Example<S> example) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Customer, R> R findBy(Example<S> example,
		Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		throw new UnsupportedOperationException();
	}
}
