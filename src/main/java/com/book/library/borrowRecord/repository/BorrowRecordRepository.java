package com.book.library.borrowRecord.repository;

import com.book.library.borrowRecord.model.BorrowRecord;
import com.book.library.borrower.model.Borrower;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {

    List<BorrowRecord> findByBorrower_Id(UUID borrowerId);

    Page<BorrowRecord> findByBorrower_Id(UUID borrowerId, Pageable pageable);

    List<BorrowRecord> findByReturnDateIsNull();

    @Query("""
        SELECT DISTINCT br.borrower 
        FROM BorrowRecord br 
        WHERE br.dueDate < :today 
        AND br.returnDate IS NULL
        """)
    List<Borrower> findBorrowersWithOverdueBooks(LocalDate today);
}
