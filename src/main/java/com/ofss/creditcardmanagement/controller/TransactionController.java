package com.ofss.creditcardmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ofss.creditcardmanagement.dto.PaymentRequest;
import com.ofss.creditcardmanagement.dto.PurchaseRequest;
import com.ofss.creditcardmanagement.entity.CardTransaction;
import com.ofss.creditcardmanagement.entity.TransactionStatus;
import com.ofss.creditcardmanagement.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    /*
     * ============================================================
     * PURCHASE
     *
     * POST /api/transactions/purchase
     * ============================================================
     */
    @PostMapping("/purchase")
    public ResponseEntity<CardTransaction> makePurchase(
            @Valid @RequestBody PurchaseRequest request) {

        CardTransaction transaction =
                transactionService.makePurchase(request);

        if (transaction.getStatus()
                == TransactionStatus.FAILED) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(transaction);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }

    /*
     * ============================================================
     * PAYMENT
     *
     * POST /api/transactions/payment
     * ============================================================
     */
    @PostMapping("/payment")
    public ResponseEntity<CardTransaction> makePayment(
            @Valid @RequestBody PaymentRequest request) {

        CardTransaction transaction =
                transactionService.makePayment(request);

        if (transaction.getStatus()
                == TransactionStatus.FAILED) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(transaction);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transaction);
    }

    /*
     * ============================================================
     * GET ALL TRANSACTIONS
     *
     * GET /api/transactions
     * ============================================================
     */
    @GetMapping
    public ResponseEntity<List<CardTransaction>>
    getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }

    /*
     * ============================================================
     * GET TRANSACTIONS BY CARD
     *
     * GET /api/transactions/card/{cardNumber}
     * ============================================================
     */
    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<List<CardTransaction>>
    getTransactionsByCard(
            @PathVariable String cardNumber) {

        return ResponseEntity.ok(
                transactionService
                        .getTransactionsByCard(cardNumber)
        );
    }
}