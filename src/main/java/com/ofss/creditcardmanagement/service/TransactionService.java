package com.ofss.creditcardmanagement.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.creditcardmanagement.dto.PaymentRequest;
import com.ofss.creditcardmanagement.dto.PurchaseRequest;
import com.ofss.creditcardmanagement.entity.CardStatus;
import com.ofss.creditcardmanagement.entity.CardTransaction;
import com.ofss.creditcardmanagement.entity.CreditCard;
import com.ofss.creditcardmanagement.entity.Merchant;
import com.ofss.creditcardmanagement.entity.TransactionStatus;
import com.ofss.creditcardmanagement.entity.TransactionType;
import com.ofss.creditcardmanagement.repository.CardTransactionRepository;
import com.ofss.creditcardmanagement.repository.CreditCardRepository;
import com.ofss.creditcardmanagement.repository.MerchantRepository;

@Service
public class TransactionService {

    private final CardTransactionRepository transactionRepository;
    private final CreditCardRepository creditCardRepository;
    private final MerchantRepository merchantRepository;

    public TransactionService(
            CardTransactionRepository transactionRepository,
            CreditCardRepository creditCardRepository,
            MerchantRepository merchantRepository) {

        this.transactionRepository = transactionRepository;
        this.creditCardRepository = creditCardRepository;
        this.merchantRepository = merchantRepository;
    }

    /*
     * ============================================================
     * PURCHASE
     * ============================================================
     */
    @Transactional
    public CardTransaction makePurchase(PurchaseRequest request) {

        CreditCard card = creditCardRepository
                .findById(request.getCardNumber())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Credit card not found: "
                                        + request.getCardNumber()
                        )
                );

        Merchant merchant = merchantRepository
                .findById(request.getMerchantId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Merchant not found with ID: "
                                        + request.getMerchantId()
                        )
                );

        BigDecimal amount = request.getAmount();

        /*
         * Card must be ACTIVE.
         */
        if (card.getCardStatus() != CardStatus.ACTIVE) {

            return saveFailedTransaction(
                    card,
                    merchant,
                    TransactionType.PURCHASE,
                    amount
            );
        }

        /*
         * Available credit must be sufficient.
         */
        if (card.getAvailableCredit()
                .compareTo(amount) < 0) {

            return saveFailedTransaction(
                    card,
                    merchant,
                    TransactionType.PURCHASE,
                    amount
            );
        }

        /*
         * Reduce available credit.
         */
        card.setAvailableCredit(
                card.getAvailableCredit()
                        .subtract(amount)
        );

        /*
         * Increase outstanding amount.
         */
        card.setOutstandingAmount(
                card.getOutstandingAmount()
                        .add(amount)
        );

        creditCardRepository.save(card);

        /*
         * Record successful purchase.
         */
        CardTransaction transaction =
                new CardTransaction();

        transaction.setCreditCard(card);

        transaction.setMerchant(merchant);

        transaction.setTransactionType(
                TransactionType.PURCHASE
        );

        transaction.setAmount(amount);

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        return transactionRepository.save(transaction);
    }

    /*
     * ============================================================
     * PAYMENT
     * ============================================================
     */
    @Transactional
    public CardTransaction makePayment(
            PaymentRequest request) {

        CreditCard card = creditCardRepository
                .findById(request.getCardNumber())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Credit card not found: "
                                        + request.getCardNumber()
                        )
                );

        BigDecimal amount = request.getAmount();

        /*
         * Payment cannot exceed outstanding balance.
         */
        if (amount.compareTo(
                card.getOutstandingAmount()) > 0) {

            return saveFailedTransaction(
                    card,
                    null,
                    TransactionType.PAYMENT,
                    amount
            );
        }

        /*
         * Reduce outstanding amount.
         */
        card.setOutstandingAmount(
                card.getOutstandingAmount()
                        .subtract(amount)
        );

        /*
         * Increase available credit.
         */
        card.setAvailableCredit(
                card.getAvailableCredit()
                        .add(amount)
        );

        creditCardRepository.save(card);

        /*
         * Record successful payment.
         */
        CardTransaction transaction =
                new CardTransaction();

        transaction.setCreditCard(card);

        /*
         * Payment has no merchant.
         */
        transaction.setMerchant(null);

        transaction.setTransactionType(
                TransactionType.PAYMENT
        );

        transaction.setAmount(amount);

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        return transactionRepository.save(transaction);
    }

    /*
     * ============================================================
     * SAVE FAILED TRANSACTION
     * ============================================================
     */
    private CardTransaction saveFailedTransaction(
            CreditCard card,
            Merchant merchant,
            TransactionType transactionType,
            BigDecimal amount) {

        CardTransaction transaction =
                new CardTransaction();

        transaction.setCreditCard(card);

        transaction.setMerchant(merchant);

        transaction.setTransactionType(
                transactionType
        );

        transaction.setAmount(amount);

        transaction.setStatus(
                TransactionStatus.FAILED
        );

        return transactionRepository.save(transaction);
    }

    /*
     * ============================================================
     * GET ALL TRANSACTIONS
     * ============================================================
     */
    @Transactional(readOnly = true)
    public List<CardTransaction> getAllTransactions() {

        return transactionRepository.findAll();
    }

    /*
     * ============================================================
     * GET TRANSACTIONS BY CARD
     * ============================================================
     */
    @Transactional(readOnly = true)
    public List<CardTransaction> getTransactionsByCard(
            String cardNumber) {

        if (!creditCardRepository.existsById(cardNumber)) {

            throw new RuntimeException(
                    "Credit card not found: "
                            + cardNumber
            );
        }

        return transactionRepository
                .findByCreditCardCardNumberOrderByTransactionDateDesc(
                        cardNumber
                );
    }
}