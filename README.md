# Library Book Management System

*Spring Boot REST API to manage a small library’s books, borrowers, and borrowing lifecycle — with realistic constraints and relational behavior. It is built using Spring Boot 3.5.7, Spring Data JPA, and in-memory H2 Database.*

---

## Overview

This project is a **Spring Boot 3.x RESTful API** that simulates a small library management system.  
It supports the following core functionalities:

- Managing **books** (add, list, update, soft delete)
- Managing **borrowers** (register, view history, overdue tracking)
- Handling **borrowing and returning workflows**
- Maintaining **fines** and **borrow limits** based on membership type
- Demonstrates **transaction management**, **relational modeling**, **DTO usage**, **pagination**, and **error handling**

---

## Core Features

### Book Management APIs

| Endpoint | Description |
|-----------|--------------|
| `POST /books` | Add a new book or increase total copies if same title exists |
| `GET /books?category=Tech&available=true&page=0&size=5&sort=title,asc` | List books with filters, pagination, and sorting |
| `PUT /books/{id}` | Update metadata or adjust available copies |
| `DELETE /books/{id}` | Soft delete a book only if no active borrow records | 

---

### Borrower Management APIs

| Endpoint | Description |
|-----------|-------------|
| `POST /borrowers` | Register a new borrower (BASIC or PREMIUM) |
| `GET /borrowers/{id}/records` | Get borrower’s full borrow history |
| `GET /borrowers/overdue` | List borrowers who have overdue books (dueDate < today & not returned) |

---

### Borrowing Workflow APIs

| Endpoint               | Description |
|------------------------|-------------|
| `POST /records/borrow` | Borrow a book with validation checks (availability, borrow limit, etc.) |
| `POST /records/return` | Return a book and calculate fine if overdue |
| `GET /records/active`  | List all currently borrowed books with borrower details and due dates |

---

## Architecture

**Architecture Pattern:** Layered (Controller → Service → Repository)

**Tech Stack:**
- Spring Boot 3.x
- Spring Data JPA + H2 Database
- Lombok (to reduce boilerplate)
- JUnit + Mockito for testing

**Entity Relationships:**
- `Book (1) ↔ (Many) BorrowRecord`
- `Borrower (1) ↔ (Many) BorrowRecord`

**Transaction Management:**  
`@Transactional` is used in borrow/return flow to ensure atomic updates and prevent race conditions.

---

## Setup and Run Instructions

### Prerequisites
- Java 17+
- Maven 3.x+
- (Optional) Postman for testing

---

### Steps to Run

```bash
# Clone repo
git clone https://github.com/Niteshprimedev/library-application.git
cd library-application

# Build & run
mvn clean package
mvn spring-boot:run
```

## Access Points

| Component         | URL                                  |
|-------------------|---------------------------------------|
| API Root Endpoint | http://localhost:8080  |
| H2 Console        | http://localhost:8080/h2-console      |
| Default DB        | jdbc:h2:mem:bookLibraryDB                 |

---

## Example Data (auto-seeded)

| Title       | Author              | Category | Total Copies | Available |
|-------------|---------------------|----------|--------------|-----------|
| Clean Code  | Robert C. Martin    | Tech     | 3            | 3         |
| The Hobbit  | J.R.R. Tolkien      | Fiction  | 2            | 2         |
| Sapiens     | Yuval Noah Harari   | History  | 5            | 5         |

---

## Approach & Thought Process

### Entity Design
Modeled relationships using `@OneToMany` and `@ManyToOne` to ensure referential integrity.

### DTOs
Used DTOs for API responses to decouple persistence from the transport layer.

### Validation & Error Handling
- Input validations via `@Valid` and constraint annotations  
- Global exception handling using `@ControllerAdvice` returning a consistent JSON structure

### Transaction Management
Borrow/return operations are handled atomically with pessimistic locking to prevent inconsistent copies during concurrent access.

### Fine Calculation
`fine = daysLate * finePerDay` (default = 10/day if FinePolicy not configured)

### Soft Delete
Books are soft deleted using a `deleted` flag only if no active borrow record exists.

### Scalability
Architecture modularized for easy integration with MySQL/PostgreSQL or a caching layer.

---

## Example API Workflows

### Add a New Book

**Request** `http://localhost:8080/books`
```json
{
  "title": "Clean Code",
  "author": "Robert Martin",
  "category": "Tech",
  "totalCopies": 3
}
```

**Response**
```json
{
  "id": "UUID",
  "title": "Clean Code",
  "author": "Robert Martin",
  "category": "Tech",
  "available": true,
  "totalCopies": 3,
  "availableCopies": 3
}
```

---

### Register a Borrower

**Request** `http://localhost:8080/borrowers`
```json
{
  "name": "Nitesh Sharma",
  "email": "niteshprimedev@gmail.com",
  "membershipType": "BASIC"
}
```

**Response**
```json
{
  "id":"UUID",
  "name": "Nitesh Sharma",
  "email": "niteshprimedev@gmail.com",
  "membershipType": "BASIC",
  "maxBorrowLimit":2
}
```

---

### Borrow a Book

**Request** `http://localhost:8080/records/borrow`
```json
{
  "borrowerId": "uuid-borrower-1",
  "bookId": "uuid-book-1"
}
```

**Response**
```json
{
  "id":"uuid-record-1",
  "bookId":"uuid-book-1",
  "bookTitle":"Clean Code",
  "borrowerId":"uuid-borrower-1",
  "borrowerName":"Nitesh Sharma",
  "borrowDate":"2025-11-17",
  "dueDate":"2025-12-01",
  "returnDate":"2025-11-17",
  "active":false,
  "fineAmount":null
}
```

---

### Return a Book

**Request** `http://localhost:8080/records/return`
```json
{
  "borrowRecordId": "uuid-record-1"
}
```

**Response**
```json
{
  "id":"uuid-record-1",
  "bookId":"uuid-book-1",
  "bookTitle":"Clean Code",
  "borrowerId":"uuid-borrower-1",
  "borrowerName":"Nitesh Sharma",
  "borrowDate":"2025-11-17",
  "dueDate":"2025-12-01",
  "returnDate":"2025-11-17",
  "active":false,  
  "fineAmount":0
}
```

---

### Error Example


**Request** `http://localhost:8080/records/borrow`

```json
{
  "borrowerId": "uuid-borrower-1",
  "bookId": "uuid-book-1"
}
```

**Response**

Borrow attempt exceeding limit:

```json
{
  "message":"Borrow limit reached",
  "status":false,
  "errors":{}
}
```

## Testing

### Run all tests
```bash
mvn test
```

## Test Coverage Includes

- Borrow success flow
- Borrow over-limit scenario
- Return success flow without fine 
- Validation and error handling

---

## Tools & Libraries Used

| Tool                          | Purpose                  |
|-------------------------------|--------------------------|
| Spring Boot                   | Application framework    |
| Spring Data JPA               | ORM & DB persistence     |
| H2                            | In-memory database       |
| Lombok                        | Boilerplate reduction    |
| JUnit / Mockito               | Testing                  |
| Maven                         | Build management         |

---

## Known Limitations / Next Steps

- No authentication/authorization (JWT can be added later)
- FinePolicy currently static (can be made configurable via CRUD API)
- No email notification system yet for overdue reminders

---

## Author

**Nitesh Sharma**  
Backend Developer | Java + Spring Boot | Distributed Systems

**Email:** niteshprimedev@gmail.com  
**GitHub:** https://github.com/niteshprimedev  
**LinkedIn:** https://linkedin.com/in/niteshprimedev

> “Clean code is not written by following rules, it’s written by thinking like the next maintainer.”
