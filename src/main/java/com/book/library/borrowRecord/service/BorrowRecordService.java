package com.book.library.borrowRecord.service;

import com.book.library.borrowRecord.dto.BorrowRecordResponse;
import com.book.library.borrowRecord.dto.BorrowRequest;
import com.book.library.borrowRecord.dto.ReturnRequest;

import java.util.List;

public interface BorrowRecordService {
    BorrowRecordResponse borrowBook(BorrowRequest request);
    BorrowRecordResponse returnBook(ReturnRequest request);
    List<BorrowRecordResponse> getActiveRecords();
}
