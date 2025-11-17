package com.book.library.borrowRecord.model;

import com.book.library.book.model.Book;
import com.book.library.borrower.model.Borrower;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "borrow_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Borrower borrower;

    @Column(nullable = false)
    private LocalDate borrowDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column
    private LocalDate returnDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal fineAmount;

    public boolean isActive() {
        return returnDate == null;
    }
}
