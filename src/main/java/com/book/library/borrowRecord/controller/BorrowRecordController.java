package com.book.library.borrowRecord.controller;

import com.book.library.borrowRecord.dto.BorrowRecordResponse;
import com.book.library.borrowRecord.dto.BorrowRequest;
import com.book.library.borrowRecord.dto.ReturnRequest;
import com.book.library.borrowRecord.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/records")
public class BorrowRecordController {

        private final BorrowRecordService borrowRecordService;

        @PostMapping("/borrow")
        public ResponseEntity<BorrowRecordResponse> borrowBook(
                @Valid @RequestBody BorrowRequest borrowRequest
        ){
            BorrowRecordResponse borrowRecordResponse = borrowRecordService.borrowBook(borrowRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(borrowRecordResponse);
        }

        @PostMapping("/return")
        public ResponseEntity<BorrowRecordResponse> returnBook(
                @Valid @RequestBody ReturnRequest returnRequest
        ){
            BorrowRecordResponse borrowRecordResponse = borrowRecordService.returnBook(returnRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(borrowRecordResponse);
        }

        @GetMapping("/active")
        public ResponseEntity<List<BorrowRecordResponse>> getActiveRecords(){
            List<BorrowRecordResponse> borrowRecordServiceActiveRecords = borrowRecordService.getActiveRecords();
            return ResponseEntity.status(HttpStatus.OK).body(borrowRecordServiceActiveRecords);
        }
    }

