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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @DisplayName("Should add new book when title-author pair doesn't exist")
    void shouldAddNewBook() {
        CreateBookRequest request = new CreateBookRequest("T1", "A1", "C1", 4);
        Book mappedBook = modelMapper.map(request, Book.class);

        when(bookRepository.findByTitleAndAuthor(anyString(), anyString())).thenReturn(null);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        BookResponse response = bookService.addBook(request);

        assertThat(response.getTitle()).isEqualTo("T1");
        assertThat(response.getTotalCopies()).isEqualTo(4);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Should increase total copies if book already exists")
    void shouldIncreaseCopiesIfBookExists() {
        CreateBookRequest request = new CreateBookRequest("T2", "A2", "C2", 4);
        Book existingBook = new Book(UUID.randomUUID(), "T2", "A2", "C2", true, 8, 4);

        when(bookRepository.findByTitleAndAuthor(anyString(), anyString())).thenReturn(existingBook);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        BookResponse response = bookService.addBook(request);

        assertThat(response.getTotalCopies()).isEqualTo(12);
        assertThat(response.getAvailableCopies()).isEqualTo(8);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    @DisplayName("Should throw exception when book not found for update")
    void shouldThrowWhenBookNotFoundForUpdate() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(eq(id))).thenReturn(Optional.empty());

        UpdateBookRequest update = new UpdateBookRequest("Title", "Author", "Category", 3);

        assertThatThrownBy(() -> bookService.updateBook(update, id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Book not found");
    }
}
