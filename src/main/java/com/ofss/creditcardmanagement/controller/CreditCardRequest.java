package com.ofss.creditcardmanagement.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ofss.creditcardmanagement.entity.CardType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreditCardRequest {

    @NotBlank(message = "Card number is required")
    @Pattern(
        regexp = "^[0-9]{16,19}$",
        message = "Card number must contain 16 to 19 digits"
    )
    private String cardNumber;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(
        value = "0.01",
        message = "Credit limit must be greater than zero"
    )
    private BigDecimal creditLimit;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    public CreditCardRequest() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}