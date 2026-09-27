package com.ofss.creditcardmanagement.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ofss.creditcardmanagement.entity.CardType;
import com.ofss.creditcardmanagement.entity.CreditCard;
import com.ofss.creditcardmanagement.service.CreditCardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(
            CreditCardService creditCardService) {

        this.creditCardService = creditCardService;
    }

    /*
     * Issue Credit Card
     *
     * POST /api/credit-cards
     */
    @PostMapping
    public ResponseEntity<CreditCard> issueCreditCard(
            @Valid @RequestBody CreditCardRequest request) {

        CreditCard creditCard = new CreditCard();

        creditCard.setCardNumber(
                request.getCardNumber()
        );

        creditCard.setCardType(
                request.getCardType()
        );

        creditCard.setCreditLimit(
                request.getCreditLimit()
        );

        creditCard.setExpiryDate(
                request.getExpiryDate()
        );

        CreditCard createdCard =
                creditCardService.issueCreditCard(
                        request.getCardNumber(),
                        request.getCustomerId(),
                        creditCard
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCard);
    }
    /*
     * Get all credit cards
     *
     * GET /api/credit-cards
     */
    @GetMapping
    public ResponseEntity<List<CreditCard>>
    getAllCreditCards() {

        return ResponseEntity.ok(
                creditCardService.getAllCreditCards()
        );
    }

    /*
     * Get card by card number
     *
     * GET /api/credit-cards/{cardNumber}
     */
    @GetMapping("/{cardNumber}")
    public ResponseEntity<CreditCard>
    getCreditCardByNumber(
            @PathVariable String cardNumber) {

        return ResponseEntity.ok(
                creditCardService
                        .getCreditCardByNumber(cardNumber)
        );
    }

    /*
     * Get cards belonging to a customer
     *
     * GET /api/credit-cards/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CreditCard>>
    getCardsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                creditCardService
                        .getCardsByCustomer(customerId)
        );
    }

    /*
     * Update card
     *
     * PUT /api/credit-cards/{cardNumber}
     */
    @PutMapping("/{cardNumber}")
    public ResponseEntity<CreditCard>
    updateCreditCard(
            @PathVariable String cardNumber,
            @Valid @RequestBody CreditCardRequest request) {

        CreditCard updateRequest =
                new CreditCard();

        updateRequest.setCardType(
                request.getCardType()
        );

        updateRequest.setCreditLimit(
                request.getCreditLimit()
        );

        updateRequest.setExpiryDate(
                request.getExpiryDate()
        );

        CreditCard updatedCard =
                creditCardService.updateCreditCard(
                        cardNumber,
                        updateRequest
                );

        return ResponseEntity.ok(updatedCard);
    }

    /*
     * Block card
     *
     * PATCH /api/credit-cards/{cardNumber}/block
     */
    @PatchMapping("/{cardNumber}/block")
    public ResponseEntity<CreditCard>
    blockCard(@PathVariable String cardNumber) {

        return ResponseEntity.ok(
                creditCardService.blockCard(cardNumber)
        );
    }

    /*
     * Unblock card
     *
     * PATCH /api/credit-cards/{cardNumber}/unblock
     */
    @PatchMapping("/{cardNumber}/unblock")
    public ResponseEntity<CreditCard>
    unblockCard(@PathVariable String cardNumber) {

        return ResponseEntity.ok(
                creditCardService.unblockCard(cardNumber)
        );
    }
}