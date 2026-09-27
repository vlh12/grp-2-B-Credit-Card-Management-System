package com.ofss.creditcardmanagement.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ofss.creditcardmanagement.entity.Customer;
import com.ofss.creditcardmanagement.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Get all customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // Get customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable Long id) {

        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create customer
    @PostMapping
    public Customer createCustomer(
            @Valid @RequestBody Customer customer) {

        return customerService.createCustomer(customer);
    }

    // Update customer
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody Customer customer) {

        if (customerService.getCustomerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        customer.setCustomerId(id);

        Customer updatedCustomer =
                customerService.updateCustomer(customer);

        return ResponseEntity.ok(updatedCustomer);
    }

    // Delete customer
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id) {

        if (customerService.getCustomerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }
}