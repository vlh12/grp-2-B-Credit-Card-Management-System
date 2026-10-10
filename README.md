# Credit Card Management System

A Java-based fintech application for managing **customers, credit cards, merchants, purchases, bill payments, transaction history, and financial reports**.

The project demonstrates enterprise application development using **Java 25, Spring Boot, Spring Data JPA, REST APIs, Hibernate, and Oracle Database 26ai**.

---

## Project Objective

The Credit Card Management System provides REST APIs that allow a bank to:

- Register and manage customers
- Add and manage merchants
- Issue and manage credit cards
- Block and unblock credit cards
- Process credit card purchases
- Process full and partial bill payments
- Track available credit
- Track outstanding balances
- Maintain complete transaction history
- Record successful and failed transactions
- Generate financial and operational reports

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java SE 25 | Programming language |
| Spring Boot 3.x | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Persistence layer |
| Hibernate | ORM / JPA implementation |
| Jakarta Validation | Request validation |
| Oracle Database 26ai | Database |
| Oracle JDBC Driver | Database connectivity |
| Maven | Build and dependency management |
| Insomnia | REST API testing |
| Git | Version control |
| GitHub | Source code repository |

---

# Architecture

The project follows a **Layered Architecture**.

```text
                    Client / Postman
                           |
                           | HTTP / JSON
                           v
                 +--------------------+
                 | Controller Layer   |
                 +--------------------+
                           |
                           v
                 +--------------------+
                 | Service Layer      |
                 | Business Rules     |
                 +--------------------+
                           |
                           v
                 +--------------------+
                 | Repository Layer   |
                 | Spring Data JPA    |
                 +--------------------+
                           |
                           v
                 +--------------------+
                 | Hibernate / JDBC   |
                 +--------------------+
                           |
                           v
                 +--------------------+
                 | Oracle Database    |
                 |      26ai          |
                 +--------------------+
```

The current application is a **single Spring Boot application using layered architecture**, not a microservices architecture.

---

# Package Structure

```text
com.ofss.creditcardmanagement
|
+-- controller
|   +-- CustomerController.java
|   +-- MerchantController.java
|   +-- CreditCardController.java
|   +-- TransactionController.java
|   +-- ReportController.java
|
+-- service
|   +-- CustomerService.java
|   +-- MerchantService.java
|   +-- CreditCardService.java
|   +-- TransactionService.java
|   +-- ReportService.java
|
+-- repository
|   +-- CustomerRepository.java
|   +-- MerchantRepository.java
|   +-- CreditCardRepository.java
|   +-- CardTransactionRepository.java
|   +-- ReportRepository.java
|
+-- entity
|   +-- Customer.java
|   +-- Merchant.java
|   +-- CreditCard.java
|   +-- CardTransaction.java
|   +-- CardType.java
|   +-- CardStatus.java
|   +-- TransactionType.java
|   +-- TransactionStatus.java
|
+-- dto
|   +-- PurchaseRequest.java
|   +-- PaymentRequest.java
|
+-- exception
|   +-- ResourceNotFoundException.java
|   +-- DuplicateResourceException.java
|   +-- InvalidTransactionException.java
|   +-- ErrorResponse.java
|   +-- GlobalExceptionHandler.java
|
+-- CreditCardManagementApplication.java
```

---

# Database Design

The application uses **Oracle Database 26ai**.

The following project-specific tables are used:

```text
CUSTOMERS_CA
MERCHANTS_CA
CREDIT_CARDS_CA
TRANSACTIONS_CA
```

## Relationships

```text
CUSTOMERS_CA
     |
     | 1 : N
     v
CREDIT_CARDS_CA
     |
     | 1 : N
     v
TRANSACTIONS_CA
     |
     | N : 1
     v
MERCHANTS_CA
```

A customer can own multiple credit cards.

A credit card can have multiple transactions.

Purchase transactions reference a merchant.

Payment transactions do not require a merchant.

---

# 1. Customer Management

Customer CRUD operations have been implemented.

## Features

- Register customer
- Retrieve customer by ID
- Retrieve all customers
- Update customer
- Delete customer
- Input validation

## APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/customers` | Register customer |
| GET | `/api/customers` | Retrieve all customers |
| GET | `/api/customers/{id}` | Retrieve customer by ID |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

## Validation

Customer validation includes:

- Customer name required
- Valid email address
- 10-digit mobile number
- PAN format validation
- Field-length validation
- Database uniqueness constraints

---

# 2. Merchant Management

Merchant CRUD operations have been implemented.

## APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/merchants` | Add merchant |
| GET | `/api/merchants` | Retrieve all merchants |
| GET | `/api/merchants/{id}` | Retrieve merchant by ID |
| PUT | `/api/merchants/{id}` | Update merchant |
| DELETE | `/api/merchants/{id}` | Delete merchant |

Merchant information includes:

- Merchant ID
- Merchant Name
- Category
- Location

---

# 3. Credit Card Management

Credit cards are associated with customers using JPA relationships.

## Card Types

```text
SILVER
GOLD
PLATINUM
```

## Card Status

```text
ACTIVE
BLOCKED
```

## Features

- Issue credit card
- Retrieve all cards
- Retrieve card by number
- Retrieve cards belonging to a customer
- Update credit card
- Block credit card
- Unblock credit card

## APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/credit-cards` | Issue credit card |
| GET | `/api/credit-cards` | Retrieve all cards |
| GET | `/api/credit-cards/{cardNumber}` | Retrieve card |
| GET | `/api/credit-cards/customer/{customerId}` | Retrieve customer's cards |
| PUT | `/api/credit-cards/{cardNumber}` | Update card |
| PATCH | `/api/credit-cards/{cardNumber}/block` | Block card |
| PATCH | `/api/credit-cards/{cardNumber}/unblock` | Unblock card |

When a new card is issued:

```text
Available Credit   = Credit Limit
Outstanding Amount = 0
Card Status        = ACTIVE
```

---

# 4. Purchase Transactions

Customers can make purchases using active credit cards.

## API

```text
POST /api/transactions/purchase
```

Example:

```json
{
    "cardNumber": "4222222222222222",
    "merchantId": 1,
    "amount": 5000
}
```

## Purchase Processing

```text
Purchase Request
       |
       v
Lock Credit Card Row
       |
       v
Find Credit Card
       |
       v
Find Merchant
       |
       v
Check Card ACTIVE
       |
       v
Check Card Expiry
       |
       v
Check Available Credit
       |
       v
Reduce Available Credit
       |
       v
Increase Outstanding Amount
       |
       v
Record PURCHASE Transaction
       |
       v
Commit
```

For a successful purchase:

```text
Available Credit =
    Available Credit - Purchase Amount

Outstanding Amount =
    Outstanding Amount + Purchase Amount
```

## Purchase Business Rules

- Card must exist
- Merchant must exist
- Card must be ACTIVE
- Blocked cards cannot make purchases
- Expired cards cannot make purchases
- Purchase amount must be greater than zero
- Available credit must be sufficient
- Available credit cannot become negative
- Successful purchases update card balances
- Successful purchases are recorded
- Failed business-rule purchases are recorded with `FAILED` status

---

# 5. Bill Payment

The application supports both:

- Partial payment
- Full payment

## API

```text
POST /api/transactions/payment
```

Example:

```json
{
    "cardNumber": "4222222222222222",
    "amount": 2000
}
```

For a successful payment:

```text
Outstanding Amount =
    Outstanding Amount - Payment Amount

Available Credit =
    Available Credit + Payment Amount
```

## Payment Rules

- Card must exist
- Payment amount must be greater than zero
- Payment cannot exceed outstanding balance
- Outstanding amount is reduced after payment
- Available credit is increased after payment
- Successful payments are recorded
- Invalid overpayment attempts are recorded as failed transactions

---

# 6. Transaction Management

The system supports:

```text
PURCHASE
PAYMENT
```

Transaction statuses:

```text
SUCCESS
FAILED
```

Each transaction contains:

- Transaction ID
- Card Number
- Transaction Type
- Amount
- Merchant for purchases
- Transaction Date and Time
- Status

## APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions/purchase` | Make purchase |
| POST | `/api/transactions/payment` | Make payment |
| GET | `/api/transactions` | Complete transaction history |
| GET | `/api/transactions/card/{cardNumber}` | Card transaction history |

---

# 7. Transaction Safety and Concurrency

Financial operations use Spring transaction management:

```java
@Transactional
```

Credit card balance updates use **pessimistic database locking**:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

The locked lookup ensures that simultaneous transactions cannot independently update the same card balance based on stale available-credit values.

Example:

```text
Available Credit = 5000

Request A: Purchase 4000
Request B: Purchase 4000

Request A
    |
    +-- Lock Card
    +-- Available = 5000
    +-- Purchase = 4000
    +-- Available = 1000
    +-- Commit
    +-- Release Lock

Request B
    |
    +-- Wait for lock
    +-- Read Available = 1000
    +-- Purchase 4000 rejected
    +-- FAILED transaction recorded
```

This helps protect the rule:

```text
Available Credit must never become negative.
```

---

# 8. Validation

Jakarta Bean Validation is used throughout the REST API.

Examples:

```java
@NotBlank
@NotNull
@Email
@Pattern
@Size
@DecimalMin
@Valid
```

Invalid requests return:

```text
HTTP 400 Bad Request
```

---

# 9. Exception Handling

Centralized exception handling is implemented using:

```java
@RestControllerAdvice
```

Custom exceptions include:

```text
ResourceNotFoundException
DuplicateResourceException
InvalidTransactionException
```

## HTTP Error Handling

| Situation | HTTP Status |
|---|---|
| Invalid request data | 400 Bad Request |
| Invalid business operation | 400 Bad Request |
| Resource not found | 404 Not Found |
| Duplicate resource | 409 Conflict |
| Unexpected server error | 500 Internal Server Error |

Example error:

```json
{
    "timestamp": "2026-09-27T12:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Credit card not found: 9999999999999999",
    "path": "/api/credit-cards/9999999999999999"
}
```

---

# 10. Reports

The project includes the required financial and operational reports.

## Reports 1-4

Existing APIs provide:

| # | Report | Endpoint |
|---|---|---|
| 1 | All customers | `GET /api/customers` |
| 2 | All credit cards | `GET /api/credit-cards` |
| 3 | All merchants | `GET /api/merchants` |
| 4 | Complete transaction history | `GET /api/transactions` |

## Reports 5-20

| # | Report | Endpoint |
|---|---|---|
| 5 | Highest outstanding customer | `/api/reports/highest-outstanding` |
| 6 | Lowest outstanding customer | `/api/reports/lowest-outstanding` |
| 7 | Merchant with highest sales | `/api/reports/highest-sales-merchant` |
| 8 | Merchant with highest transaction count | `/api/reports/highest-transaction-merchant` |
| 9 | Most frequently used card | `/api/reports/most-used-card` |
| 10 | Least frequently used card | `/api/reports/least-used-card` |
| 11 | Today's total purchases | `/api/reports/today-purchases` |
| 12 | Today's total payments | `/api/reports/today-payments` |
| 13 | Blocked cards | `/api/reports/blocked-cards` |
| 14 | Cards below 20% available credit | `/api/reports/low-available-credit` |
| 15 | Highest spending customer | `/api/reports/highest-spending-customer` |
| 16 | Customer with highest payment | `/api/reports/highest-payment-customer` |
| 17 | Total outstanding amount | `/api/reports/total-outstanding` |
| 18 | Average purchase amount | `/api/reports/average-purchase` |
| 19 | Largest purchase | `/api/reports/largest-purchase` |
| 20 | Monthly customer spending | `/api/reports/monthly-spending` |

Reports use successful financial transactions when calculating actual spending, sales, payments, usage, and averages.

---

# 11. Example Report APIs

## Total Outstanding

```text
GET /api/reports/total-outstanding
```

Example:

```json
{
    "totalOutstandingAmount": 3000.00
}
```

## Today's Purchases

```text
GET /api/reports/today-purchases
```

Example:

```json
{
    "totalPurchaseAmountToday": 5000.00
}
```

## Monthly Spending

```text
GET /api/reports/monthly-spending
```

Example:

```json
[
    {
        "customerId": 1,
        "customerName": "Rahul Sharma",
        "month": "2026-09",
        "totalSpending": 5000
    }
]
```

---

# Database Configuration

Database credentials must not be committed to Git.

`application.properties` uses environment variables:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=validate

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Configure locally:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Do **not** commit `.env`, database passwords, tokens, or other credentials.

---

# Running the Application

## Prerequisites

Install/configure:

- Java 25
- Maven
- Oracle Database 26ai
- Git
- Postman or another REST client

Ensure Oracle is running and the database environment variables are configured.

## Windows

```bash
mvnw.cmd spring-boot:run
```

## Linux / macOS

```bash
./mvnw spring-boot:run
```

The application runs by default at:

```text
http://localhost:8080
```

---

# Current Development Status

| Feature | Status |
|---|---|
| Oracle Database Schema | Completed |
| Spring Boot Setup | Completed |
| Oracle/JPA Integration | Completed |
| Customer CRUD | Completed |
| Customer Validation | Completed |
| Merchant CRUD | Completed |
| Credit Card Management | Completed |
| Customer/Card Relationship | Completed |
| Block/Unblock Card | Completed |
| Purchase Processing | Completed |
| Failed Purchase Recording | Completed |
| Partial Payment | Completed |
| Full Payment | Completed |
| Payment Transaction Recording | Completed |
| Transaction History | Completed |
| Bean Validation | Completed |
| Global Exception Handling | Completed |
| Custom Exceptions | Completed |
| Transaction Management | Completed |
| Pessimistic Concurrency Protection | Completed |
| Reports 1-20 | Implemented |
| Automated Unit Tests | Pending |
| Integration Tests | Pending |
| Complete Postman Regression Testing | Pending |
| Final Code Cleanup | Pending |

---

# Remaining Work

The primary application functionality is implemented.

The remaining development phase focuses on software quality and final verification:

1. Add unit tests
2. Add service-layer tests
3. Add REST/controller integration tests
4. Test all business rules
5. Test all report APIs
6. Prepare Postman collection
7. Perform final code cleanup
8. Finalize documentation

---

# Important Business Rules

The application enforces the following rules:

- Card numbers must be unique
- A customer may own multiple credit cards
- Purchases are allowed only on ACTIVE cards
- Blocked cards cannot perform purchases
- Expired cards cannot perform purchases
- Available credit must never become negative
- Payment cannot exceed outstanding balance
- Every successful purchase is recorded
- Every successful payment is recorded
- Failed business-rule transactions are recorded
- Purchase decreases available credit
- Purchase increases outstanding amount
- Payment increases available credit
- Payment decreases outstanding amount
- Concurrent financial operations lock the relevant credit-card row

---

# Git Workflow

After completing a feature:

```bash
git status
git add .
git commit -m "description of change"
git push
```

Example commits:

```text
feat: add customer management
feat: add merchant management
feat: add credit card management
feat: add purchase and payment processing
feat: improve exception handling
feat: add concurrency protection for card transactions
feat: add reporting module
test: add application tests
docs: update project README
```

---

# Project Status

**Core Application: Implemented**

**Reports: Implemented**

**Testing and Final Quality Checks: In Progress**

---

## Project Information

**Project:** Credit Card Management System  
**Base Package:** `com.ofss.creditcardmanagement`  
**Architecture:** Layered Spring Boot Application  
**Database:** Oracle Database 26ai  
**Build Tool:** Maven  
**Language:** Java 25
