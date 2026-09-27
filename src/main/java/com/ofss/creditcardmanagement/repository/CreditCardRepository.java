package com.ofss.creditcardmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import com.ofss.creditcardmanagement.entity.CreditCard;

import jakarta.persistence.LockModeType;

@Repository
public interface CreditCardRepository
        extends JpaRepository<CreditCard, String> {

    List<CreditCard> findByCustomerCustomerId(Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query(
            "SELECT c FROM CreditCard c WHERE c.cardNumber = :cardNumber"
    )
    java.util.Optional<CreditCard> findByCardNumberForUpdate(
            @org.springframework.data.repository.query.Param("cardNumber")
            String cardNumber
    );
}