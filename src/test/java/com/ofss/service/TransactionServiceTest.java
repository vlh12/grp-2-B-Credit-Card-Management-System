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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ofss.creditcardmanagement.dto.PaymentRequest;
import com.ofss.creditcardmanagement.dto.PurchaseRequest;
import com.ofss.creditcardmanagement.entity.CardStatus;
import com.ofss.creditcardmanagement.entity.CardTransaction;
import com.ofss.creditcardmanagement.entity.CreditCard;
import com.ofss.creditcardmanagement.entity.Merchant;
import com.ofss.creditcardmanagement.entity.TransactionStatus;
import com.ofss.creditcardmanagement.entity.TransactionType;
import com.ofss.creditcardmanagement.exception.ResourceNotFoundException;
import com.ofss.creditcardmanagement.repository.CardTransactionRepository;
import com.ofss.creditcardmanagement.repository.CreditCardRepository;
import com.ofss.creditcardmanagement.repository.MerchantRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final String CARD_NUMBER = "1234567890123456";

    @Mock private CardTransactionRepository transactionRepository;
    @Mock private CreditCardRepository creditCardRepository;
    @Mock private MerchantRepository merchantRepository;
    @InjectMocks private TransactionService service;

    private CreditCard card;
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        card = new CreditCard();
        card.setCardNumber(CARD_NUMBER);
        card.setCardStatus(CardStatus.ACTIVE);
        card.setExpiryDate(LocalDate.now().plusYears(1));
        card.setAvailableCredit(new BigDecimal("1000.00"));
        card.setOutstandingAmount(new BigDecimal("200.00"));
        merchant = new Merchant();
    }

    @Test
    void purchaseUpdatesBalancesAndRecordsSuccess() {
        stubPurchaseLookups();
        stubTransactionSave();

        CardTransaction result = service.makePurchase(purchase("150.00"));

        assertEquals(new BigDecimal("850.00"), card.getAvailableCredit());
        assertEquals(new BigDecimal("350.00"), card.getOutstandingAmount());
        assertSame(card, result.getCreditCard());
        assertSame(merchant, result.getMerchant());
        assertEquals(TransactionType.PURCHASE, result.getTransactionType());
        assertEquals(TransactionStatus.SUCCESS, result.getStatus());
        assertEquals(new BigDecimal("150.00"), result.getAmount());
        verify(creditCardRepository).save(card);
    }

    @Test
    void purchaseWithInsufficientCreditRecordsFailureWithoutChangingBalance() {
        stubPurchaseLookups();
        stubTransactionSave();

        CardTransaction result = service.makePurchase(purchase("1000.01"));

        assertEquals(TransactionStatus.FAILED, result.getStatus());
        assertEquals(TransactionType.PURCHASE, result.getTransactionType());
        assertEquals(new BigDecimal("1000.00"), card.getAvailableCredit());
        assertEquals(new BigDecimal("200.00"), card.getOutstandingAmount());
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void blockedCardPurchaseRecordsFailure() {
        card.setCardStatus(CardStatus.BLOCKED);
        stubPurchaseLookups();
        stubTransactionSave();

        CardTransaction result = service.makePurchase(purchase("50.00"));

        assertEquals(TransactionStatus.FAILED, result.getStatus());
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void expiredCardPurchaseRecordsFailure() {
        card.setExpiryDate(LocalDate.now().minusDays(1));
        stubPurchaseLookups();
        stubTransactionSave();

        CardTransaction result = service.makePurchase(purchase("50.00"));

        assertEquals(TransactionStatus.FAILED, result.getStatus());
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void purchaseWithUnknownCardThrows() {
        when(creditCardRepository.findByCardNumberForUpdate(CARD_NUMBER))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.makePurchase(purchase("50.00")));

        verify(transactionRepository, never()).save(any(CardTransaction.class));
    }

    @Test
    void purchaseWithUnknownMerchantThrows() {
        when(creditCardRepository.findByCardNumberForUpdate(CARD_NUMBER))
                .thenReturn(Optional.of(card));
        when(merchantRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.makePurchase(purchase("50.00")));

        verify(transactionRepository, never()).save(any(CardTransaction.class));
    }

    @Test
    void paymentUpdatesBalancesAndRecordsSuccess() {
        stubCardLookup();
        stubTransactionSave();

        CardTransaction result = service.makePayment(payment("75.00"));

        assertEquals(new BigDecimal("1075.00"), card.getAvailableCredit());
        assertEquals(new BigDecimal("125.00"), card.getOutstandingAmount());
        assertEquals(TransactionType.PAYMENT, result.getTransactionType());
        assertEquals(TransactionStatus.SUCCESS, result.getStatus());
        assertEquals(new BigDecimal("75.00"), result.getAmount());
        verify(creditCardRepository).save(card);
    }

    @Test
    void paymentAboveOutstandingRecordsFailure() {
        stubCardLookup();
        stubTransactionSave();

        CardTransaction result = service.makePayment(payment("200.01"));

        assertEquals(TransactionStatus.FAILED, result.getStatus());
        assertEquals(TransactionType.PAYMENT, result.getTransactionType());
        assertEquals(new BigDecimal("1000.00"), card.getAvailableCredit());
        assertEquals(new BigDecimal("200.00"), card.getOutstandingAmount());
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void transactionsForUnknownCardThrow() {
        when(creditCardRepository.existsById(CARD_NUMBER)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.getTransactionsByCard(CARD_NUMBER));
    }

    @Test
    void transactionsForKnownCardAreReturned() {
        CardTransaction transaction = new CardTransaction();
        when(creditCardRepository.existsById(CARD_NUMBER)).thenReturn(true);
        when(transactionRepository.findByCreditCardCardNumberOrderByTransactionDateDesc(CARD_NUMBER))
                .thenReturn(List.of(transaction));

        assertEquals(List.of(transaction), service.getTransactionsByCard(CARD_NUMBER));
    }

    private void stubCardLookup() {
        when(creditCardRepository.findByCardNumberForUpdate(CARD_NUMBER))
                .thenReturn(Optional.of(card));
    }

    private void stubPurchaseLookups() {
        stubCardLookup();
        when(merchantRepository.findById(7L)).thenReturn(Optional.of(merchant));
    }

    private void stubTransactionSave() {
        when(transactionRepository.save(any(CardTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private PurchaseRequest purchase(String amount) {
        PurchaseRequest request = new PurchaseRequest();
        request.setCardNumber(CARD_NUMBER);
        request.setMerchantId(7L);
        request.setAmount(new BigDecimal(amount));
        return request;
    }

    private PaymentRequest payment(String amount) {
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber(CARD_NUMBER);
        request.setAmount(new BigDecimal(amount));
        return request;
    }
}
