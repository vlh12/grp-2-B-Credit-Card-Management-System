package com.ofss.creditcardmanagement.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ofss.creditcardmanagement.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /*
     * ============================================================
     * 5. CUSTOMER WITH HIGHEST OUTSTANDING
     * ============================================================
     */
    @GetMapping("/highest-outstanding")
    public ResponseEntity<Map<String, Object>>
    highestOutstanding() {

        return ResponseEntity.ok(
                reportService
                        .getCustomerWithHighestOutstanding()
        );
    }

    /*
     * ============================================================
     * 6. CUSTOMER WITH LOWEST OUTSTANDING
     * ============================================================
     */
    @GetMapping("/lowest-outstanding")
    public ResponseEntity<Map<String, Object>>
    lowestOutstanding() {

        return ResponseEntity.ok(
                reportService
                        .getCustomerWithLowestOutstanding()
        );
    }

    /*
     * ============================================================
     * 7. MERCHANT WITH HIGHEST SALES
     * ============================================================
     */
    @GetMapping("/highest-sales-merchant")
    public ResponseEntity<Map<String, Object>>
    highestSalesMerchant() {

        return ResponseEntity.ok(
                reportService
                        .getMerchantWithHighestSales()
        );
    }

    /*
     * ============================================================
     * 8. MERCHANT WITH HIGHEST NUMBER OF TRANSACTIONS
     * ============================================================
     */
    @GetMapping("/highest-transaction-merchant")
    public ResponseEntity<Map<String, Object>>
    highestTransactionMerchant() {

        return ResponseEntity.ok(
                reportService
                        .getMerchantWithHighestTransactionCount()
        );
    }

    /*
     * ============================================================
     * 9. MOST FREQUENTLY USED CARD
     * ============================================================
     */
    @GetMapping("/most-used-card")
    public ResponseEntity<Map<String, Object>>
    mostUsedCard() {

        return ResponseEntity.ok(
                reportService
                        .getMostFrequentlyUsedCard()
        );
    }

    /*
     * ============================================================
     * 10. LEAST FREQUENTLY USED CARD
     * ============================================================
     */
    @GetMapping("/least-used-card")
    public ResponseEntity<Map<String, Object>>
    leastUsedCard() {

        return ResponseEntity.ok(
                reportService
                        .getLeastFrequentlyUsedCard()
        );
    }

    /*
     * ============================================================
     * 11. TODAY'S PURCHASE AMOUNT
     * ============================================================
     */
    @GetMapping("/today-purchases")
    public ResponseEntity<Map<String, Object>>
    todayPurchases() {

        BigDecimal amount =
                reportService.getTodayPurchaseAmount();

        return ResponseEntity.ok(
                Map.of(
                        "totalPurchaseAmountToday",
                        amount
                )
        );
    }

    /*
     * ============================================================
     * 12. TODAY'S PAYMENT AMOUNT
     * ============================================================
     */
    @GetMapping("/today-payments")
    public ResponseEntity<Map<String, Object>>
    todayPayments() {

        BigDecimal amount =
                reportService.getTodayPaymentAmount();

        return ResponseEntity.ok(
                Map.of(
                        "totalPaymentAmountToday",
                        amount
                )
        );
    }

    /*
     * ============================================================
     * 13. ALL BLOCKED CARDS
     * ============================================================
     */
    @GetMapping("/blocked-cards")
    public ResponseEntity<List<Map<String, Object>>>
    blockedCards() {

        return ResponseEntity.ok(
                reportService.getAllBlockedCards()
        );
    }

    /*
     * ============================================================
     * 14. CARDS BELOW 20% AVAILABLE CREDIT
     * ============================================================
     */
    @GetMapping("/low-available-credit")
    public ResponseEntity<List<Map<String, Object>>>
    lowAvailableCredit() {

        return ResponseEntity.ok(
                reportService
                        .getCardsBelowTwentyPercentCredit()
        );
    }

    /*
     * ============================================================
     * 15. HIGHEST SPENDING CUSTOMER
     * ============================================================
     */
    @GetMapping("/highest-spending-customer")
    public ResponseEntity<Map<String, Object>>
    highestSpendingCustomer() {

        return ResponseEntity.ok(
                reportService
                        .getCustomerWithHighestSpending()
        );
    }

    /*
     * ============================================================
     * 16. CUSTOMER WITH HIGHEST PAYMENT
     * ============================================================
     */
    @GetMapping("/highest-payment-customer")
    public ResponseEntity<Map<String, Object>>
    highestPaymentCustomer() {

        return ResponseEntity.ok(
                reportService
                        .getCustomerWithHighestPayment()
        );
    }

    /*
     * ============================================================
     * 17. TOTAL OUTSTANDING AMOUNT
     * ============================================================
     */
    @GetMapping("/total-outstanding")
    public ResponseEntity<Map<String, Object>>
    totalOutstanding() {

        BigDecimal amount =
                reportService.getTotalOutstandingAmount();

        return ResponseEntity.ok(
                Map.of(
                        "totalOutstandingAmount",
                        amount
                )
        );
    }

    /*
     * ============================================================
     * 18. AVERAGE PURCHASE AMOUNT
     * ============================================================
     */
    @GetMapping("/average-purchase")
    public ResponseEntity<Map<String, Object>>
    averagePurchase() {

        BigDecimal amount =
                reportService.getAveragePurchaseAmount();

        return ResponseEntity.ok(
                Map.of(
                        "averagePurchaseAmount",
                        amount
                )
        );
    }

    /*
     * ============================================================
     * 19. LARGEST PURCHASE
     * ============================================================
     */
    @GetMapping("/largest-purchase")
    public ResponseEntity<Map<String, Object>>
    largestPurchase() {

        return ResponseEntity.ok(
                reportService.getLargestPurchase()
        );
    }

    /*
     * ============================================================
     * 20. MONTHLY SPENDING SUMMARY
     * ============================================================
     */
    @GetMapping("/monthly-spending")
    public ResponseEntity<List<Map<String, Object>>>
    monthlySpending() {

        return ResponseEntity.ok(
                reportService.getMonthlySpendingSummary()
        );
    }
}