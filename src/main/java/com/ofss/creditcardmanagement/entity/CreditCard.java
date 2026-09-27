package com.ofss.creditcardmanagement.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "CREDIT_CARDS_CA")
public class CreditCard {

    @Id
    @NotBlank(message = "Card number is required")
    @Pattern(
        regexp = "^[0-9]{16,19}$",
        message = "Card number must contain 16 to 19 digits"
    )
    @Column(name = "CARD_NUMBER", length = 19, nullable = false)
    private String cardNumber;

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "CUSTOMER_ID",
        nullable = false
    )
    private Customer customer;

    @NotNull(message = "Card type is required")
    @Enumerated(EnumType.STRING)
    @Column(
        name = "CARD_TYPE",
        nullable = false,
        length = 20
    )
    private CardType cardType;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(
        value = "0.01",
        message = "Credit limit must be greater than zero"
    )
    @Column(
        name = "CREDIT_LIMIT",
        nullable = false,
        precision = 15,
        scale = 2
    )
    private BigDecimal creditLimit;

    @NotNull(message = "Available credit is required")
    @DecimalMin(
        value = "0.00",
        message = "Available credit cannot be negative"
    )
    @Column(
        name = "AVAILABLE_CREDIT",
        nullable = false,
        precision = 15,
        scale = 2
    )
    private BigDecimal availableCredit;

    @NotNull(message = "Outstanding amount is required")
    @DecimalMin(
        value = "0.00",
        message = "Outstanding amount cannot be negative"
    )
    @Column(
        name = "OUTSTANDING_AMOUNT",
        nullable = false,
        precision = 15,
        scale = 2
    )
    private BigDecimal outstandingAmount = BigDecimal.ZERO;

    @NotNull(message = "Expiry date is required")
    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    @NotNull(message = "Card status is required")
    @Enumerated(EnumType.STRING)
    @Column(
        name = "CARD_STATUS",
        nullable = false,
        length = 20
    )
    private CardStatus cardStatus;

    public CreditCard() {
    }

    public CreditCard(
            String cardNumber,
            Customer customer,
            CardType cardType,
            BigDecimal creditLimit,
            BigDecimal availableCredit,
            BigDecimal outstandingAmount,
            LocalDate expiryDate,
            CardStatus cardStatus) {

        this.cardNumber = cardNumber;
        this.customer = customer;
        this.cardType = cardType;
        this.creditLimit = creditLimit;
        this.availableCredit = availableCredit;
        this.outstandingAmount = outstandingAmount;
        this.expiryDate = expiryDate;
        this.cardStatus = cardStatus;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public BigDecimal getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(BigDecimal availableCredit) {
        this.availableCredit = availableCredit;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }
}