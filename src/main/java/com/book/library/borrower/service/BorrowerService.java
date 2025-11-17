package com.book.library.borrower.service;

import com.book.library.borrower.dto.BorrowRecordData;
import com.book.library.borrower.dto.BorrowerData;
import com.book.library.borrower.dto.BorrowerResponse;
import com.book.library.borrower.dto.CreateBorrowerRequest;

import java.util.UUID;

public interface BorrowerService {
    BorrowerResponse addBorrower(CreateBorrowerRequest createBorrowerRequest);
    BorrowRecordData getRecords(UUID borrowerId, int page, int size, String sortBy, String sortDir);
    BorrowerData getOverdueBorrowers(int page, int size, String sortBy, String sortDir);
}
