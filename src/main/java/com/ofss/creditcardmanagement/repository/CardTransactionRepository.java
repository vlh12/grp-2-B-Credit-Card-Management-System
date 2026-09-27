package com.ofss.creditcardmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ofss.creditcardmanagement.entity.CardTransaction;

@Repository
public interface CardTransactionRepository
        extends JpaRepository<CardTransaction, Long> {

    List<CardTransaction> findByCreditCardCardNumberOrderByTransactionDateDesc(
            String cardNumber
    );

    List<CardTransaction> findByTransactionTypeOrderByTransactionDateDesc(
            com.ofss.creditcardmanagement.entity.TransactionType transactionType
    );
}