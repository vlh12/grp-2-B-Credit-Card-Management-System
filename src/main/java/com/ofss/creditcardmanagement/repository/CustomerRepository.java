package com.ofss.creditcardmanagement.repository;

import com.ofss.creditcardmanagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}