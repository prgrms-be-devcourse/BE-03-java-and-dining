package com.prgms.allen.dining.domain.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgms.allen.dining.domain.customer.entity.Customer;
import com.prgms.allen.dining.domain.customer.entity.CustomerType;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByIdAndCustomerType(Long id, CustomerType customerType);
}
