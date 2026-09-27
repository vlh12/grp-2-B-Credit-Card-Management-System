package com.ofss.creditcardmanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.creditcardmanagement.entity.Customer;
import com.ofss.creditcardmanagement.exception.DuplicateResourceException;
import com.ofss.creditcardmanagement.exception.ResourceNotFoundException;
import com.ofss.creditcardmanagement.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Transactional
    public Customer createCustomer(Customer customer) {

        try {
            return customerRepository.save(customer);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException(
                    "Customer email, mobile number, or PAN already exists"
            );
        }
    }

    @Transactional
    public Customer updateCustomer(
            Long customerId,
            Customer customer) {

        Customer existingCustomer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with ID: "
                                                + customerId
                                )
                        );

        existingCustomer.setCustomerName(
                customer.getCustomerName()
        );

        existingCustomer.setEmailAddress(
                customer.getEmailAddress()
        );

        existingCustomer.setMobileNumber(
                customer.getMobileNumber()
        );

        existingCustomer.setPanNumber(
                customer.getPanNumber()
        );

        try {
            return customerRepository.save(existingCustomer);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateResourceException(
                    "Customer email, mobile number, or PAN already exists"
            );
        }
    }

    @Transactional
    public void deleteCustomer(Long customerId) {

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with ID: "
                                                + customerId
                                )
                        );

        customerRepository.delete(customer);
    }
}