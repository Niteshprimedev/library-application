package com.book.library.book.service;

import com.book.library.book.dto.BookData;
import com.book.library.book.dto.CreateBookRequest;
import com.book.library.book.dto.BookResponse;
import com.book.library.book.dto.UpdateBookRequest;

import java.util.UUID;

public interface BookService {
    BookData getBooks();
    BookResponse addBook(CreateBookRequest createBookRequest);
    BookResponse updateBook(UpdateBookRequest updateBookRequest, UUID id);
    BookResponse removeBook(UUID id);
}
