package com.ofss.creditcardmanagement.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.creditcardmanagement.repository.ReportRepository;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /*
     * 5. Highest outstanding
     */
    public Map<String, Object> getCustomerWithHighestOutstanding() {

        List<Object[]> rows =
                reportRepository.findCustomerWithHighestOutstanding();

        return rows.isEmpty()
                ? Map.of("message", "No customer data available")
                : customerOutstandingResponse(rows.get(0));
    }

    /*
     * 6. Lowest outstanding
     */
    public Map<String, Object> getCustomerWithLowestOutstanding() {

        List<Object[]> rows =
                reportRepository.findCustomerWithLowestOutstanding();

        return rows.isEmpty()
                ? Map.of("message", "No customer data available")
                : customerOutstandingResponse(rows.get(0));
    }

    /*
     * 7. Highest merchant sales
     */
    public Map<String, Object> getMerchantWithHighestSales() {

        List<Object[]> rows =
                reportRepository.findMerchantWithHighestSales();

        if (rows.isEmpty()) {
            return Map.of(
                    "message",
                    "No successful purchase data available"
            );
        }

        Object[] row = rows.get(0);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("merchantId", row[0]);
        response.put("merchantName", row[1]);
        response.put("category", row[2]);
        response.put("location", row[3]);
        response.put("totalSales", row[4]);

        return response;
    }

    /*
     * 8. Highest merchant transaction count
     */
    public Map<String, Object>
    getMerchantWithHighestTransactionCount() {

        List<Object[]> rows =
                reportRepository.findMerchantWithHighestTransactionCount();

        if (rows.isEmpty()) {
            return Map.of(
                    "message",
                    "No successful purchase data available"
            );
        }

        Object[] row = rows.get(0);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("merchantId", row[0]);
        response.put("merchantName", row[1]);
        response.put("transactionCount", row[2]);

        return response;
    }

    /*
     * 9. Most frequently used card
     */
    public Map<String, Object> getMostFrequentlyUsedCard() {

        List<Object[]> rows =
                reportRepository.findMostFrequentlyUsedCard();

        if (rows.isEmpty()) {
            return Map.of(
                    "message",
                    "No successful purchase data available"
            );
        }

        Object[] row = rows.get(0);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("cardNumber", row[0]);
        response.put("customerName", row[1]);
        response.put("usageCount", row[2]);

        return response;
    }

    /*
     * 10. Least frequently used card
     */
    public Map<String, Object> getLeastFrequentlyUsedCard() {

        List<Object[]> rows =
                reportRepository.findLeastFrequentlyUsedCard();

        if (rows.isEmpty()) {
            return Map.of(
                    "message",
                    "No credit cards available"
            );
        }

        Object[] row = rows.get(0);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("cardNumber", row[0]);
        response.put("customerName", row[1]);
        response.put("usageCount", row[2]);

        return response;
    }

    /*
     * 11. Today's purchase amount
     */
    public BigDecimal getTodayPurchaseAmount() {
        return reportRepository.findTodayPurchaseAmount();
    }

    /*
     * 12. Today's payment amount
     */
    public BigDecimal getTodayPaymentAmount() {
        return reportRepository.findTodayPaymentAmount();
    }

    /*
     * 13. Blocked cards
     */
    public List<Map<String, Object>> getAllBlockedCards() {

        List<Object[]> rows =
                reportRepository.findAllBlockedCards();

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Object[] row : rows) {

            Map<String, Object> card =
                    new LinkedHashMap<>();

            card.put("cardNumber", row[0]);
            card.put("customerId", row[1]);
            card.put("customerName", row[2]);
            card.put("cardType", row[3]);
            card.put("creditLimit", row[4]);
            card.put("availableCredit", row[5]);
            card.put("outstandingAmount", row[6]);
            card.put("expiryDate", row[7]);
            card.put("cardStatus", row[8]);

            response.add(card);
        }

        return response;
    }

    /*
     * 14. Cards below 20% available credit
     */
    public List<Map<String, Object>>
    getCardsBelowTwentyPercentCredit() {

        List<Object[]> rows =
                reportRepository.findCardsBelowTwentyPercentCredit();

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Object[] row : rows) {

            Map<String, Object> card =
                    new LinkedHashMap<>();

            card.put("cardNumber", row[0]);
            card.put("customerName", row[1]);
            card.put("creditLimit", row[2]);
            card.put("availableCredit", row[3]);
            card.put("outstandingAmount", row[4]);
            card.put("cardStatus", row[5]);

            response.add(card);
        }

        return response;
    }

    /*
     * 15. Highest spending customer
     */
    public Map<String, Object> getCustomerWithHighestSpending() {

        List<Object[]> rows =
                reportRepository.findCustomerWithHighestSpending();

        if (rows.isEmpty()) {
            return Map.of(
                    "message",
                    "No successful purchase data available"
            );
        }

        Object[] row = rows.get(0);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("customerId", row[0]);
        response.put("customerName", row[1]);
        response.put("totalSpent", row[2]);

        return response;
    }

    /*
     * 16. Highest payment customer
     */
    public Map<String, Object> getCustomerWithHighestPayment() {

        List<Object[]> rows =
                reportRepository.findCustomerWithHighestPayment();

        if (rows.isEmpty()) {
            return Map.of(
                    "message",
                    "No successful payment data available"
            );
        }

        Object[] row = rows.get(0);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("customerId", row[0]);
        response.put("customerName", row[1]);
        response.put("paymentAmount", row[2]);
        response.put("paymentDate", row[3]);

        return response;
    }

    /*
     * 17. Total outstanding
     */
    public BigDecimal getTotalOutstandingAmount() {
        return reportRepository.findTotalOutstandingAmount();
    }

    /*
     * 18. Average purchase
     */
    public BigDecimal getAveragePurchaseAmount() {
        return reportRepository.findAveragePurchaseAmount();
    }

    /*
     * 19. Largest purchase
     */
    public Map<String, Object> getLargestPurchase() {

        List<Object[]> rows =
                reportRepository.findLargestPurchase();

        if (rows.isEmpty()) {
            return Map.of(
                    "message",
                    "No successful purchase data available"
            );
        }

        Object[] row = rows.get(0);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("transactionId", row[0]);
        response.put("cardNumber", row[1]);
        response.put("amount", row[2]);
        response.put("merchantId", row[3]);
        response.put("transactionDate", row[4]);
        response.put("status", row[5]);

        return response;
    }

    /*
     * 20. Monthly spending
     */
    public List<Map<String, Object>>
    getMonthlySpendingSummary() {

        List<Object[]> rows =
                reportRepository.findMonthlySpendingSummary();

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Object[] row : rows) {

            Map<String, Object> spending =
                    new LinkedHashMap<>();

            spending.put("customerId", row[0]);
            spending.put("customerName", row[1]);
            spending.put("month", row[2]);
            spending.put("totalSpending", row[3]);

            response.add(spending);
        }

        return response;
    }

    /*
     * Common response for reports 5 and 6.
     */
    private Map<String, Object> customerOutstandingResponse(
            Object[] row) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("customerId", row[0]);
        response.put("customerName", row[1]);
        response.put("emailAddress", row[2]);
        response.put("outstandingAmount", row[3]);

        return response;
    }
}