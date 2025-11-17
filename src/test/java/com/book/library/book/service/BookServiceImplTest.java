package com.book.library.book.service;

import com.book.library.book.dto.BookResponse;
import com.book.library.book.dto.CreateBookRequest;
import com.book.library.book.dto.UpdateBookRequest;
import com.book.library.book.model.Book;
import com.book.library.book.repository.BookRepository;
import com.book.library.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BookServiceImplTest {

    private BookRepository bookRepository;
    private ModelMapper modelMapper;
    private BookServiceImpl bookService;

    @BeforeEach
    void setup() {
        bookRepository = mock(BookRepository.class);
        modelMapper = new ModelMapper();
        bookService = new BookServiceImpl(bookRepository, modelMapper);
    }

    @Test
    @DisplayName("Should add a completely new book when title-author does not exist")
    void shouldAddNewBook() {

        CreateBookRequest createBookRequest = new CreateBookRequest("T1", "A1", "C1", 4);

        when(bookRepository.findByTitleAndAuthor("T1", "A1"))
                .thenReturn(Optional.empty());

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BookResponse bookResponse = bookService.addBook(createBookRequest);

        assertThat(bookResponse.getTitle()).isEqualTo("T1");
        assertThat(bookResponse.getTotalCopies()).isEqualTo(4);
        assertThat(bookResponse.getAvailableCopies()).isEqualTo(4);
        assertThat(bookResponse.isAvailable()).isTrue();

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should increase copies when book already exists")
    void shouldIncreaseCopiesIfBookExists() {

        CreateBookRequest createBookRequest = new CreateBookRequest("T2", "A2", "C2", 4);

        Book existingBook = new Book(
                UUID.randomUUID(),
                "T2",
                "A2",
                "C2",
                true,
                8,
                3,
                new ArrayList<>()
        );

        when(bookRepository.findByTitleAndAuthor("T2", "A2"))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BookResponse bookResponse = bookService.addBook(createBookRequest);

        assertThat(bookResponse.getTotalCopies()).isEqualTo(12);
        assertThat(bookResponse.getAvailableCopies()).isEqualTo(7);
        assertThat(bookResponse.isAvailable()).isTrue();

        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating a non-existing book")
    void shouldThrowWhenBookNotFoundForUpdate() {

        UUID id = UUID.randomUUID();

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        UpdateBookRequest updateBookRequest = new UpdateBookRequest("NewT", "NewA", "NewC", 3);

        assertThatThrownBy(() -> bookService.updateBook(updateBookRequest, id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book")
                .hasMessageContaining("id");
    }
}