package com.ofss.creditcardmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "CUSTOMERS_CA")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    @Size(
        max = 100,
        message = "Customer name cannot exceed 100 characters"
    )
    @Column(
        name = "CUSTOMER_NAME",
        nullable = false,
        length = 100
    )
    private String customerName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    @Size(
        max = 150,
        message = "Email address cannot exceed 150 characters"
    )
    @Column(
        name = "EMAIL_ADDRESS",
        nullable = false,
        unique = true,
        length = 150
    )
    private String emailAddress;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Mobile number must contain exactly 10 digits"
    )
    @Column(
        name = "MOBILE_NUMBER",
        nullable = false,
        unique = true,
        length = 15
    )
    private String mobileNumber;

    @NotBlank(message = "PAN number is required")
    @Pattern(
        regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$",
        message = "PAN number must be in valid format, for example ABCDE1234F"
    )
    @Column(
        name = "PAN_NUMBER",
        nullable = false,
        unique = true,
        length = 10
    )
    private String panNumber;

    // JPA requires a no-argument constructor
    public Customer() {
    }

    public Customer(
            String customerName,
            String emailAddress,
            String mobileNumber,
            String panNumber) {

        this.customerName = customerName;
        this.emailAddress = emailAddress;
        this.mobileNumber = mobileNumber;
        this.panNumber = panNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }
    
    
}