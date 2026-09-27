package com.ofss.creditcardmanagement.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PurchaseRequest {

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotNull(message = "Merchant ID is required")
    private Long merchantId;

    @NotNull(message = "Purchase amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Purchase amount must be greater than zero"
    )
    private BigDecimal amount;

    public PurchaseRequest() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}