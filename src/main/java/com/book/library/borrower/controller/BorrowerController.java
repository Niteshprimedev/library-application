package com.book.library.borrower.controller;

import com.book.library.borrower.dto.BorrowRecordData;
import com.book.library.borrower.dto.BorrowerData;
import com.book.library.borrower.dto.BorrowerResponse;
import com.book.library.borrower.dto.CreateBorrowerRequest;
import com.book.library.borrower.service.BorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    @PostMapping("")
    public ResponseEntity<BorrowerResponse> addBorrower(
            @Valid @RequestBody CreateBorrowerRequest createBorrowerRequest
    ){
        BorrowerResponse borrowerResponse = borrowerService.addBorrower(createBorrowerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowerResponse);
    }

    @GetMapping("/{id}/records")
    public ResponseEntity<BorrowRecordData> getRecords(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        BorrowRecordData borrowRecordData = borrowerService.getRecords(id, page, size, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(borrowRecordData);
    }

    @GetMapping("/overdue")
    public ResponseEntity<BorrowerData> getOverdueBorrowers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ){
        BorrowerData borrowerData = borrowerService.getOverdueBorrowers(page, size, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(borrowerData);
    }
}
