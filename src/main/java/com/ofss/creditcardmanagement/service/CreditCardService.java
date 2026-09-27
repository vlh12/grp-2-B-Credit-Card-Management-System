package com.ofss.creditcardmanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.creditcardmanagement.entity.CardStatus;
import com.ofss.creditcardmanagement.entity.CreditCard;
import com.ofss.creditcardmanagement.entity.Customer;
import com.ofss.creditcardmanagement.exception.DuplicateResourceException;
import com.ofss.creditcardmanagement.exception.InvalidTransactionException;
import com.ofss.creditcardmanagement.exception.ResourceNotFoundException;
import com.ofss.creditcardmanagement.repository.CreditCardRepository;
import com.ofss.creditcardmanagement.repository.CustomerRepository;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CustomerRepository customerRepository;

    public CreditCardService(
            CreditCardRepository creditCardRepository,
            CustomerRepository customerRepository) {

        this.creditCardRepository = creditCardRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<CreditCard> getAllCreditCards() {
        return creditCardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CreditCard getCreditCardByNumber(
            String cardNumber) {

        return creditCardRepository.findById(cardNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Credit card not found: "
                                        + cardNumber
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<CreditCard> getCardsByCustomer(
            Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found with ID: "
                            + customerId
            );
        }

        return creditCardRepository
                .findByCustomerCustomerId(customerId);
    }

    @Transactional
    public CreditCard issueCreditCard(
            String cardNumber,
            Long customerId,
            CreditCard creditCard) {

        if (creditCardRepository.existsById(cardNumber)) {
            throw new DuplicateResourceException(
                    "Credit card already exists: "
                            + cardNumber
            );
        }

        Customer customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with ID: "
                                                + customerId
                                )
                        );

        validateCreditDetails(creditCard);

        creditCard.setCardNumber(cardNumber);
        creditCard.setCustomer(customer);

        creditCard.setAvailableCredit(
                creditCard.getCreditLimit()
        );

        creditCard.setOutstandingAmount(
                BigDecimal.ZERO
        );

        creditCard.setCardStatus(
                CardStatus.ACTIVE
        );

        return creditCardRepository.save(creditCard);
    }

    @Transactional
    public CreditCard updateCreditCard(
            String cardNumber,
            CreditCard request) {

        CreditCard existingCard =
                creditCardRepository.findById(cardNumber)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Credit card not found: "
                                                + cardNumber
                                )
                        );

        validateCreditDetails(request);

        existingCard.setCardType(
                request.getCardType()
        );

        existingCard.setCreditLimit(
                request.getCreditLimit()
        );

        existingCard.setExpiryDate(
                request.getExpiryDate()
        );

        BigDecimal newAvailableCredit =
                existingCard.getCreditLimit()
                        .subtract(
                                existingCard.getOutstandingAmount()
                        );

        if (newAvailableCredit
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new InvalidTransactionException(
                    "Credit limit cannot be less than "
                    + "outstanding amount"
            );
        }

        existingCard.setAvailableCredit(
                newAvailableCredit
        );

        return creditCardRepository.save(existingCard);
    }

    @Transactional
    public CreditCard blockCard(String cardNumber) {

        CreditCard card =
                getCreditCardByNumber(cardNumber);

        card.setCardStatus(CardStatus.BLOCKED);

        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard unblockCard(String cardNumber) {

        CreditCard card =
                getCreditCardByNumber(cardNumber);

        card.setCardStatus(CardStatus.ACTIVE);

        return creditCardRepository.save(card);
    }

    @Transactional(readOnly = true)
    public boolean cardExists(String cardNumber) {
        return creditCardRepository.existsById(cardNumber);
    }

    private void validateCreditDetails(
            CreditCard creditCard) {

        if (creditCard.getCreditLimit() == null) {
            throw new InvalidTransactionException(
                    "Credit limit is required"
            );
        }

        if (creditCard.getCreditLimit()
                .compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidTransactionException(
                    "Credit limit must be greater than zero"
            );
        }

        if (creditCard.getExpiryDate() == null) {
            throw new InvalidTransactionException(
                    "Expiry date is required"
            );
        }

        if (creditCard.getExpiryDate()
                .isBefore(LocalDate.now())) {

            throw new InvalidTransactionException(
                    "Expiry date must be in the future"
            );
        }

        if (creditCard.getCardType() == null) {
            throw new InvalidTransactionException(
                    "Card type is required"
            );
        }
    }
}