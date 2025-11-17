package com.book.library.borrowRecord.service;

import com.book.library.book.model.Book;
import com.book.library.book.repository.BookRepository;
import com.book.library.borrowRecord.dto.BorrowRecordResponse;
import com.book.library.borrowRecord.dto.BorrowRequest;
import com.book.library.borrowRecord.dto.ReturnRequest;
import com.book.library.borrowRecord.model.BorrowRecord;
import com.book.library.borrowRecord.repository.BorrowRecordRepository;
import com.book.library.borrower.model.Borrower;
import com.book.library.borrower.repository.BorrowerRepository;
import com.book.library.exceptions.APIException;
import com.book.library.exceptions.ResourceNotFoundException;
import com.book.library.finePolicy.repository.FinePolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {

    private static final BigDecimal DEFAULT_CATEGORY_FINE_PER_DAY = BigDecimal.valueOf(10);

    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowerRepository borrowerRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;
    private final FinePolicyRepository finePolicyRepository;

    @Override
    @Transactional
    public BorrowRecordResponse borrowBook(BorrowRequest borrowRequest) {
        Borrower borrower = borrowerRepository.findById(borrowRequest.getBorrowerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Borrower","id",borrowRequest.getBorrowerId()));

        Book book = bookRepository.findById(borrowRequest.getBookId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book","id",borrowRequest.getBookId()));

        long activeCount = borrowRecordRepository.findByBorrower_Id(borrower.getId())
                .stream()
                .filter(BorrowRecord::isActive)
                .count();

        if(activeCount >= borrower.getMaxBorrowLimit()) {
            throw new APIException("Borrow limit reached");
        }

        if(book.getAvailableCopies() <= 0){
            throw new APIException("No copies available to borrow.");
        }

        book.borrowOneCopy();
        bookRepository.save(book);

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .borrower(borrower)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        BorrowRecord savedBookRecord = borrowRecordRepository.save(borrowRecord);
        return toResponse(savedBookRecord);
    }

    @Override
    @Transactional
    public BorrowRecordResponse returnBook(ReturnRequest returnRequest) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(returnRequest.getBorrowRecordId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("BorrowRecord","id",returnRequest.getBorrowRecordId()));

        if(!borrowRecord.isActive()) {
            throw new APIException("Already returned");
        }

        borrowRecord.setReturnDate(LocalDate.now());

        BigDecimal fineAmount = calcFineAmount(borrowRecord);

        borrowRecord.setFineAmount(fineAmount);

        Book book = borrowRecord.getBook();
        book.returnOneCopy();
        bookRepository.save(book);

        BorrowRecord savedBorrowRecord = borrowRecordRepository.save(borrowRecord);
        return toResponse(savedBorrowRecord);
    }

    @Override
    public List<BorrowRecordResponse> getActiveRecords() {
        List<BorrowRecordResponse> borrowRecordResponseList = borrowRecordRepository.findByReturnDateIsNull()
                .stream()
                .map(this::toResponse)
                .toList();

         return borrowRecordResponseList;
    }

    private BigDecimal calcFineAmount(BorrowRecord borrowRecord) {

        if (borrowRecord.getReturnDate().isBefore(borrowRecord.getDueDate()) ||
                borrowRecord.getReturnDate().isEqual(borrowRecord.getDueDate())) {
            return BigDecimal.ZERO;
        }

        long daysLate = ChronoUnit.DAYS.between(
                borrowRecord.getDueDate(),
                borrowRecord.getReturnDate()
        );

        String category = borrowRecord.getBook().getCategory();

        BigDecimal finePerDay = finePolicyRepository.findByCategory(category)
                .map(policy -> policy.getFinePerDay())
                .orElse(DEFAULT_CATEGORY_FINE_PER_DAY);

        BigDecimal fineAmount = finePerDay.multiply(BigDecimal.valueOf(daysLate));

        return fineAmount;
    }

    private BorrowRecordResponse toResponse(BorrowRecord borrowRecord){
        return BorrowRecordResponse.builder()
                .id(borrowRecord.getId())
                .bookId(borrowRecord.getBook().getId())
                .bookTitle(borrowRecord.getBook().getTitle())
                .borrowerId(borrowRecord.getBorrower().getId())
                .borrowerName(borrowRecord.getBorrower().getName())
                .borrowDate(borrowRecord.getBorrowDate())
                .dueDate(borrowRecord.getDueDate())
                .returnDate(borrowRecord.getReturnDate())
                .active(borrowRecord.isActive())
                .fineAmount(borrowRecord.getFineAmount())
                .build();
    }
}
