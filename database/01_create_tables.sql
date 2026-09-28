-- ============================================================
-- CREDIT CARD MANAGEMENT SYSTEM
-- Oracle Database 26ai
-- Schema Creation Script
-- ============================================================


-- ============================================================
-- 1. CUSTOMERS
-- ============================================================

CREATE TABLE customers_ca (
    customer_id      NUMBER GENERATED ALWAYS AS IDENTITY,
    customer_name    VARCHAR2(100) NOT NULL,
    email_address    VARCHAR2(150) NOT NULL,
    mobile_number    VARCHAR2(15) NOT NULL,
    pan_number       VARCHAR2(10) NOT NULL,

    CONSTRAINT pk_customers_ca
        PRIMARY KEY (customer_id),

    CONSTRAINT uk_customers_ca_email
        UNIQUE (email_address),

    CONSTRAINT uk_customers_ca_mobile
        UNIQUE (mobile_number),

    CONSTRAINT uk_customers_ca_pan
        UNIQUE (pan_number)
);


-- ============================================================
-- 2. MERCHANTS
-- ============================================================

CREATE TABLE merchants_ca (
    merchant_id      NUMBER GENERATED ALWAYS AS IDENTITY,
    merchant_name    VARCHAR2(150) NOT NULL,
    category         VARCHAR2(100) NOT NULL,
    location         VARCHAR2(200) NOT NULL,

    CONSTRAINT pk_merchants_ca
        PRIMARY KEY (merchant_id)
);


-- ============================================================
-- 3. CREDIT CARDS
-- ============================================================

CREATE TABLE credit_cards_ca (
    card_number          VARCHAR2(19),
    customer_id          NUMBER NOT NULL,
    card_type            VARCHAR2(20) NOT NULL,
    credit_limit         NUMBER(15,2) NOT NULL,
    available_credit     NUMBER(15,2) NOT NULL,
    outstanding_amount   NUMBER(15,2) DEFAULT 0 NOT NULL,
    expiry_date          DATE NOT NULL,
    card_status          VARCHAR2(20) DEFAULT 'ACTIVE' NOT NULL,

    CONSTRAINT pk_credit_cards_ca
        PRIMARY KEY (card_number),

    CONSTRAINT fk_cards_ca_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers_ca(customer_id),

    CONSTRAINT ck_cards_ca_type
        CHECK (
            card_type IN ('SILVER', 'GOLD', 'PLATINUM')
        ),

    CONSTRAINT ck_cards_ca_status
        CHECK (
            card_status IN ('ACTIVE', 'BLOCKED')
        ),

    CONSTRAINT ck_cards_ca_limit
        CHECK (
            credit_limit > 0
        ),

    CONSTRAINT ck_cards_ca_available
        CHECK (
            available_credit >= 0
            AND available_credit <= credit_limit
        ),

    CONSTRAINT ck_cards_ca_outstanding
        CHECK (
            outstanding_amount >= 0
            AND outstanding_amount <= credit_limit
        )
);


-- ============================================================
-- 4. TRANSACTIONS
-- ============================================================

CREATE TABLE transactions_ca (
    transaction_id      NUMBER GENERATED ALWAYS AS IDENTITY,
    card_number         VARCHAR2(19) NOT NULL,
    transaction_type    VARCHAR2(20) NOT NULL,
    amount              NUMBER(15,2) NOT NULL,
    merchant_id         NUMBER,
    transaction_date    TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    status              VARCHAR2(20) NOT NULL,

    CONSTRAINT pk_transactions_ca
        PRIMARY KEY (transaction_id),

    CONSTRAINT fk_transactions_ca_card
        FOREIGN KEY (card_number)
        REFERENCES credit_cards_ca(card_number),

    CONSTRAINT fk_transactions_ca_merchant
        FOREIGN KEY (merchant_id)
        REFERENCES merchants_ca(merchant_id),

    CONSTRAINT ck_transactions_ca_type
        CHECK (
            transaction_type IN ('PURCHASE', 'PAYMENT')
        ),

    CONSTRAINT ck_transactions_ca_status
        CHECK (
            status IN ('SUCCESS', 'FAILED')
        ),

    CONSTRAINT ck_transactions_ca_amount
        CHECK (
            amount > 0
        ),

    CONSTRAINT ck_transactions_ca_merchant
        CHECK (
            (transaction_type = 'PURCHASE'
                AND merchant_id IS NOT NULL)
            OR
            (transaction_type = 'PAYMENT'
                AND merchant_id IS NULL)
        )
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_cards_ca_customer
    ON credit_cards_ca(customer_id);

CREATE INDEX idx_trans_ca_card
    ON transactions_ca(card_number);

CREATE INDEX idx_trans_ca_merchant
    ON transactions_ca(merchant_id);

CREATE INDEX idx_trans_ca_date
    ON transactions_ca(transaction_date);

CREATE INDEX idx_trans_ca_type
    ON transactions_ca(transaction_type);
