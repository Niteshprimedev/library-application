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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BorrowRecordServiceImplTest {

    private BorrowRecordRepository borrowRecordRepository;
    private BorrowerRepository borrowerRepository;
    private BookRepository bookRepository;
    private FinePolicyRepository finePolicyRepository;
    private ModelMapper modelMapper;
    private BorrowRecordServiceImpl borrowService;

    @BeforeEach
    void setup() {
        borrowRecordRepository = mock(BorrowRecordRepository.class);
        borrowerRepository = mock(BorrowerRepository.class);
        bookRepository = mock(BookRepository.class);
        finePolicyRepository = mock(FinePolicyRepository.class);
        modelMapper = new ModelMapper();
        borrowService = new BorrowRecordServiceImpl(
                borrowRecordRepository, borrowerRepository, bookRepository, modelMapper, finePolicyRepository);
    }

    // Borrow Tests
    @Test
    @DisplayName("Should throw when borrower not found")
    void shouldThrowWhenBorrowerNotFound() {
        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        BorrowRequest borrowRequest = new BorrowRequest(borrowerId, bookId);

        when(borrowerRepository.findById(borrowerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.borrowBook(borrowRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Borrower");
    }

    @Test
    @DisplayName("Should throw when borrower reached max borrow limit")
    void shouldThrowWhenBorrowLimitReached() {

        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        Borrower borrower = new Borrower();
        borrower.setId(borrowerId);
        borrower.setMaxBorrowLimit(1);
        borrower.setBorrowRecords(new ArrayList<>());

        Book book = new Book();
        book.setId(bookId);
        book.setAvailableCopies(1);
        book.setBorrowRecords(new ArrayList<>());

        BorrowRequest borrowRequest = new BorrowRequest(borrowerId, bookId);

        BorrowRecord activeRecord = BorrowRecord.builder()
                .id(UUID.randomUUID())
                .borrowDate(LocalDate.now())
                .build();

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowRecordRepository.findByBorrower_Id(borrowerId))
                .thenReturn(List.of(activeRecord));

        assertThatThrownBy(() -> borrowService.borrowBook(borrowRequest))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("Borrow limit reached");
    }

    @Test
    @DisplayName("Should borrow book successfully")
    void shouldBorrowBookSuccessfully() {

        UUID borrowerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        Borrower borrower = new Borrower();
        borrower.setId(borrowerId);
        borrower.setMaxBorrowLimit(5);
        borrower.setBorrowRecords(new ArrayList<>());

        Book book = new Book();
        book.setId(bookId);
        book.setAvailableCopies(3);
        book.setBorrowRecords(new ArrayList<>());

        BorrowRequest borrowRequest = new BorrowRequest(borrowerId, bookId);

        when(borrowerRepository.findById(borrowerId)).thenReturn(Optional.of(borrower));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(borrowRecordRepository.findByBorrower_Id(borrowerId)).thenReturn(List.of());
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowRecordResponse borrowRecordResponse = borrowService.borrowBook(borrowRequest);

        assertThat(borrowRecordResponse.getBookId()).isEqualTo(bookId);
        assertThat(borrowRecordResponse.getBorrowerId()).isEqualTo(borrowerId);

        // copies should reduce
        assertThat(book.getAvailableCopies()).isEqualTo(2);
        assertThat(book.isAvailable()).isTrue();

        // due date
        assertThat(borrowRecordResponse.getDueDate()).isEqualTo(LocalDate.now().plusDays(14));

        verify(bookRepository).save(book);
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
    }

    // Return Tests
    @Test
    @DisplayName("Should throw when return record not found")
    void shouldThrowWhenReturnRecordNotFound() {
        UUID recordId = UUID.randomUUID();
        ReturnRequest returnRequest = new ReturnRequest(recordId);

        when(borrowRecordRepository.findById(recordId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowService.returnBook(returnRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("BorrowRecord");
    }

    @Test
    @DisplayName("Should return book successfully without fine")
    void shouldReturnBookSuccessfully_NoFine() {

        UUID recordId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        Book book = new Book();
        book.setId(bookId);
        book.setAvailableCopies(1);
        book.setBorrowRecords(new ArrayList<>());

        Borrower borrower = new Borrower();
        borrower.setId(UUID.randomUUID());
        borrower.setName("Test User");
        borrower.setBorrowRecords(new ArrayList<>());

        BorrowRecord record = BorrowRecord.builder()
                .id(recordId)
                .book(book)
                .borrower(borrower)
                .borrowDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(3))
                .build();

        ReturnRequest returnRequest = new ReturnRequest(recordId);

        when(borrowRecordRepository.findById(recordId)).thenReturn(Optional.of(record));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        BorrowRecordResponse borrowRecordResponse = borrowService.returnBook(returnRequest);

        assertThat(book.getAvailableCopies()).isEqualTo(2);
        assertThat(record.getFineAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(borrowRecordResponse.getFineAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(borrowRecordResponse.getBorrowerName()).isEqualTo("Test User");
    }
}