package com.ofss.creditcardmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "MERCHANTS_CA")
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MERCHANT_ID")
    private Long merchantId;

    @NotBlank(message = "Merchant name is required")
    @Size(
        max = 150,
        message = "Merchant name cannot exceed 150 characters"
    )
    @Column(
        name = "MERCHANT_NAME",
        nullable = false,
        length = 150
    )
    private String merchantName;

    @NotBlank(message = "Category is required")
    @Size(
        max = 100,
        message = "Category cannot exceed 100 characters"
    )
    @Column(
        name = "CATEGORY",
        nullable = false,
        length = 100
    )
    private String category;

    @NotBlank(message = "Location is required")
    @Size(
        max = 200,
        message = "Location cannot exceed 200 characters"
    )
    @Column(
        name = "LOCATION",
        nullable = false,
        length = 200
    )
    private String location;

    public Merchant() {
    }

    public Merchant(
            String merchantName,
            String category,
            String location) {

        this.merchantName = merchantName;
        this.category = category;
        this.location = location;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}