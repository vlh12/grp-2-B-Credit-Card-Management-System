package com.ofss.creditcardmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ofss.creditcardmanagement.entity.CardStatus;
import com.ofss.creditcardmanagement.entity.CardType;
import com.ofss.creditcardmanagement.entity.CreditCard;
import com.ofss.creditcardmanagement.entity.Customer;
import com.ofss.creditcardmanagement.exception.DuplicateResourceException;
import com.ofss.creditcardmanagement.exception.InvalidTransactionException;
import com.ofss.creditcardmanagement.exception.ResourceNotFoundException;
import com.ofss.creditcardmanagement.repository.CreditCardRepository;
import com.ofss.creditcardmanagement.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CreditcardServiceTest {

    private static final String CARD_NUMBER = "1234567890123456";

    @Mock private CreditCardRepository creditCardRepository;
    @Mock private CustomerRepository customerRepository;
    @InjectMocks private CreditCardService service;

    @Test
    void issueCardSetsInitialBalancesAndStatus() {
        Customer customer = new Customer();
        CreditCard request = validCard();
        when(creditCardRepository.existsById(CARD_NUMBER)).thenReturn(false);
        when(customerRepository.findById(3L)).thenReturn(Optional.of(customer));
        when(creditCardRepository.save(request)).thenReturn(request);

        CreditCard result = service.issueCreditCard(CARD_NUMBER, 3L, request);

        assertSame(customer, result.getCustomer());
        assertEquals(CARD_NUMBER, result.getCardNumber());
        assertEquals(new BigDecimal("1000.00"), result.getAvailableCredit());
        assertEquals(BigDecimal.ZERO, result.getOutstandingAmount());
        assertEquals(CardStatus.ACTIVE, result.getCardStatus());
        verify(creditCardRepository).save(request);
    }

    @Test
    void issueDuplicateCardThrows() {
        when(creditCardRepository.existsById(CARD_NUMBER)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> service.issueCreditCard(CARD_NUMBER, 3L, validCard()));

        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void issueCardForUnknownCustomerThrows() {
        when(creditCardRepository.existsById(CARD_NUMBER)).thenReturn(false);
        when(customerRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.issueCreditCard(CARD_NUMBER, 3L, validCard()));
    }

    @Test
    void issueCardWithZeroLimitThrows() {
        CreditCard request = validCard();
        request.setCreditLimit(BigDecimal.ZERO);
        when(creditCardRepository.existsById(CARD_NUMBER)).thenReturn(false);
        when(customerRepository.findById(3L)).thenReturn(Optional.of(new Customer()));

        assertThrows(InvalidTransactionException.class,
                () -> service.issueCreditCard(CARD_NUMBER, 3L, request));

        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void issueCardWithPastExpiryThrows() {
        CreditCard request = validCard();
        request.setExpiryDate(LocalDate.now().minusDays(1));
        when(creditCardRepository.existsById(CARD_NUMBER)).thenReturn(false);
        when(customerRepository.findById(3L)).thenReturn(Optional.of(new Customer()));

        assertThrows(InvalidTransactionException.class,
                () -> service.issueCreditCard(CARD_NUMBER, 3L, request));
    }

    @Test
    void updateCardRecalculatesAvailableCredit() {
        CreditCard existing = validCard();
        existing.setOutstandingAmount(new BigDecimal("200.00"));
        CreditCard request = validCard();
        request.setCreditLimit(new BigDecimal("1500.00"));
        request.setCardType(CardType.PLATINUM);
        when(creditCardRepository.findById(CARD_NUMBER)).thenReturn(Optional.of(existing));
        when(creditCardRepository.save(existing)).thenReturn(existing);

        CreditCard result = service.updateCreditCard(CARD_NUMBER, request);

        assertEquals(new BigDecimal("1300.00"), result.getAvailableCredit());
        assertEquals(new BigDecimal("200.00"), result.getOutstandingAmount());
        assertEquals(CardType.PLATINUM, result.getCardType());
    }

    @Test
    void updateRejectsLimitBelowOutstandingBalance() {
        CreditCard existing = validCard();
        existing.setOutstandingAmount(new BigDecimal("800.00"));
        CreditCard request = validCard();
        request.setCreditLimit(new BigDecimal("700.00"));
        when(creditCardRepository.findById(CARD_NUMBER)).thenReturn(Optional.of(existing));

        assertThrows(InvalidTransactionException.class,
                () -> service.updateCreditCard(CARD_NUMBER, request));

        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void blockCardChangesStatus() {
        CreditCard card = validCard();
        when(creditCardRepository.findById(CARD_NUMBER)).thenReturn(Optional.of(card));
        when(creditCardRepository.save(card)).thenReturn(card);

        assertEquals(CardStatus.BLOCKED, service.blockCard(CARD_NUMBER).getCardStatus());
    }

    @Test
    void unblockCardChangesStatus() {
        CreditCard card = validCard();
        card.setCardStatus(CardStatus.BLOCKED);
        when(creditCardRepository.findById(CARD_NUMBER)).thenReturn(Optional.of(card));
        when(creditCardRepository.save(card)).thenReturn(card);

        assertEquals(CardStatus.ACTIVE, service.unblockCard(CARD_NUMBER).getCardStatus());
    }

    private CreditCard validCard() {
        CreditCard card = new CreditCard();
        card.setCardType(CardType.GOLD);
        card.setCreditLimit(new BigDecimal("1000.00"));
        card.setExpiryDate(LocalDate.now().plusYears(1));
        return card;
    }
}
