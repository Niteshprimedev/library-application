package com.book.library.borrowRecord.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordResponse {
    private UUID id;
    private UUID bookId;
    private String bookTitle;
    private UUID borrowerId;
    private String borrowerName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean active;
    private BigDecimal fineAmount;
}
