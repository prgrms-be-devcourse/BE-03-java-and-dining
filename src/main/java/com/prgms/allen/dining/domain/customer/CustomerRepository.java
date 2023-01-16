package com.prgms.allen.dining.domain.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
