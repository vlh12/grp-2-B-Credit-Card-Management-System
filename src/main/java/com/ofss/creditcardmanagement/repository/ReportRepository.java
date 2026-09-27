package com.ofss.creditcardmanagement.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ofss.creditcardmanagement.entity.CreditCard;

@Repository
public interface ReportRepository
        extends JpaRepository<CreditCard, String> {

    /*
     * ============================================================
     * 5. CUSTOMER WITH HIGHEST OUTSTANDING BALANCE
     * ============================================================
     */
    @Query(value = """
            SELECT
                c.customer_id,
                c.customer_name,
                c.email_address,
                SUM(cc.outstanding_amount) AS outstanding_amount
            FROM customers_ca c
            JOIN credit_cards_ca cc
                ON c.customer_id = cc.customer_id
            GROUP BY
                c.customer_id,
                c.customer_name,
                c.email_address
            ORDER BY outstanding_amount DESC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findCustomerWithHighestOutstanding();


    /*
     * ============================================================
     * 6. CUSTOMER WITH LOWEST OUTSTANDING BALANCE
     * ============================================================
     */
    @Query(value = """
            SELECT
                c.customer_id,
                c.customer_name,
                c.email_address,
                SUM(cc.outstanding_amount) AS outstanding_amount
            FROM customers_ca c
            JOIN credit_cards_ca cc
                ON c.customer_id = cc.customer_id
            GROUP BY
                c.customer_id,
                c.customer_name,
                c.email_address
            ORDER BY outstanding_amount ASC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findCustomerWithLowestOutstanding();


    /*
     * ============================================================
     * 7. MERCHANT WITH HIGHEST SALES AMOUNT
     * ============================================================
     */
    @Query(value = """
            SELECT
                m.merchant_id,
                m.merchant_name,
                m.category,
                m.location,
                SUM(t.amount) AS total_sales
            FROM merchants_ca m
            JOIN transactions_ca t
                ON m.merchant_id = t.merchant_id
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
            GROUP BY
                m.merchant_id,
                m.merchant_name,
                m.category,
                m.location
            ORDER BY total_sales DESC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findMerchantWithHighestSales();


    /*
     * ============================================================
     * 8. MERCHANT WITH HIGHEST NUMBER OF TRANSACTIONS
     * ============================================================
     */
    @Query(value = """
            SELECT
                m.merchant_id,
                m.merchant_name,
                COUNT(t.transaction_id) AS transaction_count
            FROM merchants_ca m
            JOIN transactions_ca t
                ON m.merchant_id = t.merchant_id
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
            GROUP BY
                m.merchant_id,
                m.merchant_name
            ORDER BY transaction_count DESC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findMerchantWithHighestTransactionCount();


    /*
     * ============================================================
     * 9. MOST FREQUENTLY USED CREDIT CARD
     * ============================================================
     */
    @Query(value = """
            SELECT
                cc.card_number,
                c.customer_name,
                COUNT(t.transaction_id) AS usage_count
            FROM credit_cards_ca cc
            JOIN customers_ca c
                ON cc.customer_id = c.customer_id
            JOIN transactions_ca t
                ON cc.card_number = t.card_number
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
            GROUP BY
                cc.card_number,
                c.customer_name
            ORDER BY usage_count DESC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findMostFrequentlyUsedCard();


    /*
     * ============================================================
     * 10. LEAST FREQUENTLY USED CREDIT CARD
     *
     * LEFT JOIN includes cards with zero purchases.
     * ============================================================
     */
    @Query(value = """
            SELECT
                cc.card_number,
                c.customer_name,
                COUNT(
                    CASE
                        WHEN t.transaction_type = 'PURCHASE'
                         AND t.status = 'SUCCESS'
                        THEN t.transaction_id
                    END
                ) AS usage_count
            FROM credit_cards_ca cc
            JOIN customers_ca c
                ON cc.customer_id = c.customer_id
            LEFT JOIN transactions_ca t
                ON cc.card_number = t.card_number
            GROUP BY
                cc.card_number,
                c.customer_name
            ORDER BY usage_count ASC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findLeastFrequentlyUsedCard();


    /*
     * ============================================================
     * 11. TOTAL PURCHASE AMOUNT FOR TODAY
     * ============================================================
     */
    @Query(value = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions_ca t
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
              AND t.transaction_date >= TRUNC(SYSDATE)
              AND t.transaction_date < TRUNC(SYSDATE) + 1
            """, nativeQuery = true)
    BigDecimal findTodayPurchaseAmount();


    /*
     * ============================================================
     * 12. TOTAL PAYMENT AMOUNT FOR TODAY
     * ============================================================
     */
    @Query(value = """
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transactions_ca t
            WHERE t.transaction_type = 'PAYMENT'
              AND t.status = 'SUCCESS'
              AND t.transaction_date >= TRUNC(SYSDATE)
              AND t.transaction_date < TRUNC(SYSDATE) + 1
            """, nativeQuery = true)
    BigDecimal findTodayPaymentAmount();


    /*
     * ============================================================
     * 13. ALL BLOCKED CREDIT CARDS
     * ============================================================
     */
    @Query(value = """
            SELECT
                cc.card_number,
                c.customer_id,
                c.customer_name,
                cc.card_type,
                cc.credit_limit,
                cc.available_credit,
                cc.outstanding_amount,
                cc.expiry_date,
                cc.card_status
            FROM credit_cards_ca cc
            JOIN customers_ca c
                ON cc.customer_id = c.customer_id
            WHERE cc.card_status = 'BLOCKED'
            ORDER BY cc.card_number
            """, nativeQuery = true)
    List<Object[]> findAllBlockedCards();


    /*
     * ============================================================
     * 14. CARDS WITH AVAILABLE CREDIT BELOW 20%
     * ============================================================
     */
    @Query(value = """
            SELECT
                cc.card_number,
                c.customer_name,
                cc.credit_limit,
                cc.available_credit,
                cc.outstanding_amount,
                cc.card_status
            FROM credit_cards_ca cc
            JOIN customers_ca c
                ON cc.customer_id = c.customer_id
            WHERE cc.available_credit <
                  (cc.credit_limit * 0.20)
            ORDER BY cc.available_credit ASC
            """, nativeQuery = true)
    List<Object[]> findCardsBelowTwentyPercentCredit();


    /*
     * ============================================================
     * 15. CUSTOMER WHO HAS SPENT THE HIGHEST AMOUNT
     * ============================================================
     */
    @Query(value = """
            SELECT
                c.customer_id,
                c.customer_name,
                SUM(t.amount) AS total_spent
            FROM customers_ca c
            JOIN credit_cards_ca cc
                ON c.customer_id = cc.customer_id
            JOIN transactions_ca t
                ON cc.card_number = t.card_number
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
            GROUP BY
                c.customer_id,
                c.customer_name
            ORDER BY total_spent DESC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findCustomerWithHighestSpending();


    /*
     * ============================================================
     * 16. CUSTOMER WHO MADE THE HIGHEST PAYMENT
     * ============================================================
     *
     * This identifies the customer associated with the
     * single largest successful payment transaction.
     * ============================================================
     */
    @Query(value = """
            SELECT
                c.customer_id,
                c.customer_name,
                t.amount AS payment_amount,
                t.transaction_date
            FROM customers_ca c
            JOIN credit_cards_ca cc
                ON c.customer_id = cc.customer_id
            JOIN transactions_ca t
                ON cc.card_number = t.card_number
            WHERE t.transaction_type = 'PAYMENT'
              AND t.status = 'SUCCESS'
            ORDER BY t.amount DESC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findCustomerWithHighestPayment();


    /*
     * ============================================================
     * 17. TOTAL OUTSTANDING AMOUNT
     * ============================================================
     */
    @Query(value = """
            SELECT COALESCE(SUM(outstanding_amount), 0)
            FROM credit_cards_ca
            """, nativeQuery = true)
    BigDecimal findTotalOutstandingAmount();


    /*
     * ============================================================
     * 18. AVERAGE PURCHASE TRANSACTION AMOUNT
     * ============================================================
     */
    @Query(value = """
            SELECT COALESCE(AVG(t.amount), 0)
            FROM transactions_ca t
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
            """, nativeQuery = true)
    BigDecimal findAveragePurchaseAmount();


    /*
     * ============================================================
     * 19. LARGEST PURCHASE TRANSACTION
     * ============================================================
     */
    @Query(value = """
            SELECT
                t.transaction_id,
                t.card_number,
                t.amount,
                t.merchant_id,
                t.transaction_date,
                t.status
            FROM transactions_ca t
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
            ORDER BY t.amount DESC
            FETCH FIRST 1 ROWS ONLY
            """, nativeQuery = true)
    List<Object[]> findLargestPurchase();


    /*
     * ============================================================
     * 20. MONTHLY SPENDING SUMMARY OF EVERY CUSTOMER
     * ============================================================
     */
    @Query(value = """
            SELECT
                c.customer_id,
                c.customer_name,
                TO_CHAR(
                    t.transaction_date,
                    'YYYY-MM'
                ) AS spending_month,
                SUM(t.amount) AS total_spending
            FROM customers_ca c
            JOIN credit_cards_ca cc
                ON c.customer_id = cc.customer_id
            JOIN transactions_ca t
                ON cc.card_number = t.card_number
            WHERE t.transaction_type = 'PURCHASE'
              AND t.status = 'SUCCESS'
            GROUP BY
                c.customer_id,
                c.customer_name,
                TO_CHAR(
                    t.transaction_date,
                    'YYYY-MM'
                )
            ORDER BY
                c.customer_id,
                spending_month
            """, nativeQuery = true)
    List<Object[]> findMonthlySpendingSummary();
}