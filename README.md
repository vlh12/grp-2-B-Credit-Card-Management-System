# Credit Card Management System

A Java-based fintech application for managing customers, credit cards, merchants, purchases, payments, and credit card transaction history.

The project is being developed as a hands-on enterprise application using **Java 25, Spring Boot, Spring Data JPA, REST APIs, and Oracle Database 26ai**.

---

## Project Objective

The objective of the Credit Card Management System is to provide REST APIs through which a bank can:

- Manage customers
- Manage merchants
- Issue and manage credit cards
- Block and unblock cards
- Process credit card purchases
- Process full and partial bill payments
- Maintain purchase and payment transaction history
- Track available credit and outstanding balances
- Generate financial and operational reports

---

## Technology Stack

| Technology | Usage |
|---|---|
| Java SE 25 | Programming language |
| Spring Boot 3.x | Application framework |
| Spring Web | REST API development |
| Spring Data JPA | Persistence layer |
| Hibernate | JPA implementation / ORM |
| Jakarta Validation | Request validation |
| Oracle Database 26ai | Database |
| Oracle JDBC Driver | Oracle connectivity |
| Maven | Build and dependency management |
| Postman | REST API testing |
| Git / GitHub | Version control |

---

## Architecture

The project follows a layered architecture.

```text
Client / Postman
       |
       v
+-------------------+
| Controller Layer  |
+-------------------+
       |
       v
+-------------------+
|   Service Layer   |
+-------------------+
       |
       v
+-------------------+
| Repository Layer  |
+-------------------+
       |
       v
+-------------------+
| Spring Data JPA   |
|    / Hibernate    |
+-------------------+
       |
       v
+-------------------+
| Oracle Database   |
+-------------------+
```

The application is currently designed as a **single Spring Boot application using layered architecture**, rather than microservices.

---

## Project Package Structure

```text
com.ofss.creditcardmanagement
|
+-- controller
|   +-- CustomerController.java
|   +-- MerchantController.java
|   +-- CreditCardController.java
|   +-- TransactionController.java
|
+-- service
|   +-- CustomerService.java
|   +-- MerchantService.java
|   +-- CreditCardService.java
|   +-- TransactionService.java
|
+-- repository
|   +-- CustomerRepository.java
|   +-- MerchantRepository.java
|   +-- CreditCardRepository.java
|   +-- CardTransactionRepository.java
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
|   +-- GlobalExceptionHandler.java
|
+-- CreditCardManagementApplication.java
```

---

# Database Design

The application uses Oracle Database 26ai.

The following application-specific tables have been created:

```text
CUSTOMERS_CA
MERCHANTS_CA
CREDIT_CARDS_CA
TRANSACTIONS_CA
```

## Database Relationships

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

A customer may own multiple credit cards.

A credit card may have multiple transactions.

Purchase transactions are associated with a merchant.

Payment transactions do not require a merchant.

---

# Implemented Features

## 1. Customer Management

Customer management has been implemented using:

```text
CustomerController
       |
CustomerService
       |
CustomerRepository
       |
CUSTOMERS_CA
```

### Supported Operations

- Register a customer
- Retrieve all customers
- Retrieve customer by ID
- Update customer
- Delete customer

### REST APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/customers` | Create customer |
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

### Customer Validation

Validation currently includes:

- Customer name is required
- Valid email address
- 10-digit mobile number
- PAN format validation
- Maximum field lengths
- Database uniqueness constraints for relevant customer fields

---

# 2. Merchant Management

Merchant CRUD operations have been implemented.

### Supported Operations

- Add merchant
- Retrieve all merchants
- Retrieve merchant by ID
- Update merchant
- Delete merchant

### REST APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/merchants` | Add merchant |
| GET | `/api/merchants` | Get all merchants |
| GET | `/api/merchants/{id}` | Get merchant by ID |
| PUT | `/api/merchants/{id}` | Update merchant |
| DELETE | `/api/merchants/{id}` | Delete merchant |

---

# 3. Credit Card Management

Credit card management has been implemented using a JPA relationship between customers and credit cards.

### Card Types

```text
SILVER
GOLD
PLATINUM
```

### Card Status

```text
ACTIVE
BLOCKED
```

### Implemented Operations

- Issue a credit card
- Retrieve all credit cards
- Retrieve a card by card number
- Retrieve cards belonging to a customer
- Update credit card information
- Block a credit card
- Unblock a credit card

### REST APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/credit-cards` | Issue credit card |
| GET | `/api/credit-cards` | Get all cards |
| GET | `/api/credit-cards/{cardNumber}` | Get card details |
| GET | `/api/credit-cards/customer/{customerId}` | Get customer's cards |
| PUT | `/api/credit-cards/{cardNumber}` | Update card |
| PATCH | `/api/credit-cards/{cardNumber}/block` | Block card |
| PATCH | `/api/credit-cards/{cardNumber}/unblock` | Unblock card |

When a new card is issued:

```text
Outstanding Amount = 0
Available Credit   = Credit Limit
Card Status        = ACTIVE
```

Available credit and outstanding balance are controlled by the application rather than being directly supplied by API clients.

---

# 4. Purchase Transactions

Purchase transaction processing has been implemented.

### Purchase Flow

```text
Purchase Request
       |
       v
Find Credit Card
       |
       v
Find Merchant
       |
       v
Check Card Status
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
```

### Business Rules Implemented

- Card must exist
- Merchant must exist
- Card must be `ACTIVE`
- Blocked cards cannot make purchases
- Purchase amount must be greater than zero
- Sufficient available credit must exist
- Available credit cannot become negative
- Successful purchases update card balances
- Successful purchases are recorded
- Failed business-rule purchases are recorded with `FAILED` status

### Purchase API

```text
POST /api/transactions/purchase
```

Example request:

```json
{
    "cardNumber": "4222222222222222",
    "merchantId": 1,
    "amount": 5000
}
```

For a successful purchase:

```text
Available Credit   = Available Credit - Purchase Amount
Outstanding Amount = Outstanding Amount + Purchase Amount
```

---

# 5. Bill Payment

Credit card bill payment has been implemented.

The system supports both:

- Partial payment
- Full payment

### Payment API

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
Outstanding Amount = Outstanding Amount - Payment Amount
Available Credit   = Available Credit + Payment Amount
```

### Payment Rules

- Card must exist
- Payment amount must be greater than zero
- Payment cannot exceed outstanding balance
- Successful payment updates the card balance
- Every successful payment generates a transaction record
- Failed overpayment attempts are recorded as failed transactions

---

# 6. Transaction Management

The system currently supports two transaction types:

```text
PURCHASE
PAYMENT
```

Transaction statuses:

```text
SUCCESS
FAILED
```

Transactions contain information including:

- Transaction ID
- Card
- Transaction type
- Amount
- Merchant for purchases
- Transaction date/time
- Transaction status

### Transaction APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions/purchase` | Make purchase |
| POST | `/api/transactions/payment` | Make payment |
| GET | `/api/transactions` | Get transaction history |
| GET | `/api/transactions/card/{cardNumber}` | Get card transaction history |

---

# 7. Validation

Jakarta Bean Validation is being used for API request validation.

Examples include:

```java
@NotBlank
@NotNull
@Email
@Pattern
@Size
@DecimalMin
```

Invalid requests return HTTP:

```text
400 Bad Request
```

---

# 8. Exception Handling

A global exception handler has been added using:

```java
@RestControllerAdvice
```

Validation exceptions are converted into readable API responses.

The current implementation still uses generic runtime exceptions for some service-layer errors. Custom domain exceptions are planned as part of the remaining work.

---

# 9. Transaction Management

Spring transaction management is used for financial operations:

```java
@Transactional
```

A successful purchase performs both:

```text
Update Credit Card
        +
Insert Transaction
```

within the service operation.

Payment processing similarly updates the card balance and records the payment transaction.

Further concurrency protection is planned before final completion.

---

# Current Project Status

| Module | Status |
|---|---|
| Oracle Database Setup | Completed |
| Spring Boot Setup | Completed |
| Oracle + JPA Connection | Completed |
| Customer CRUD | Completed |
| Customer Validation | Completed |
| Merchant CRUD | Completed |
| Credit Card Management | Completed |
| Customer/Card Relationship | Completed |
| Block/Unblock Card | Completed |
| Purchase Transactions | Completed |
| Failed Purchase Recording | Completed |
| Partial Payment | Completed |
| Full Payment | Completed |
| Payment Transaction Recording | Completed |
| Transaction History | Completed |
| Basic Global Exception Handling | Completed |
| Custom Domain Exceptions | Pending |
| Concurrent Balance Protection | Pending |
| Reports | Pending |
| Automated Tests | Pending |
| Final API Testing | Pending |
| Final Documentation/Cleanup | Pending |

---

# Reports To Be Implemented

The project requirements include the following reports:

1. Display all customer details
2. Display all credit card details
3. Display all merchant details
4. Display complete transaction history
5. Customers with the highest outstanding balance
6. Customers with the lowest outstanding balance
7. Merchant with the highest sales amount
8. Merchant with the highest number of transactions
9. Most frequently used credit card
10. Least frequently used credit card
11. Total purchase amount for today
12. Total payment amount for today
13. All blocked credit cards
14. Cards whose available credit is below 20% of credit limit
15. Customer who has spent the highest amount
16. Customer who made the highest payment
17. Total outstanding amount across all customers
18. Average purchase transaction amount
19. Largest purchase transaction
20. Monthly spending summary of every customer

These reports are part of the next development phase.

---

# Database Configuration

Database credentials should not be committed to Git.

`application.properties` can reference environment variables:

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

Configure the following environment variables locally:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Do not commit real database passwords or other credentials to the repository.

---

# Running the Application

## Prerequisites

Ensure the following are installed/configured:

- Java 25
- Maven
- Oracle Database 26ai
- Git
- Postman or another REST client

Configure the database environment variables and ensure the Oracle database is running.

Run using Maven Wrapper on Windows:

```bash
mvnw.cmd spring-boot:run
```

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

Or run:

```text
CreditCardManagementApplication.java
```

from the IDE.

The application runs by default on:

```text
http://localhost:8080
```

---

# Development Progress

The main CRUD and financial transaction functionality is currently implemented.

The next development phase will focus on:

1. Custom exception classes and improved HTTP error handling
2. Concurrency protection for financial balance updates
3. Implementation of all required reports
4. Automated unit/integration testing
5. Complete API testing
6. Final project cleanup and documentation

---

## Repository

**Project:** Credit Card Management System

**Group:** `com.ofss`

**Architecture:** Layered Spring Boot Application

**Database:** Oracle Database 26ai
