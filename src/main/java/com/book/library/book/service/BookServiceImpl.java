package com.book.library.book.service;

import com.book.library.book.dto.BookData;
import com.book.library.book.dto.CreateBookRequest;
import com.book.library.book.dto.BookResponse;
import com.book.library.book.dto.UpdateBookRequest;
import com.book.library.book.model.Book;
import com.book.library.book.repository.BookRepository;
import com.book.library.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public BookData getBooks(String category, Boolean available, int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // fetch all paginated books
        Page<Book> bookPage = bookRepository.findAll(pageable);

        List<Book> booksWithCopies = bookPage.getContent().stream()
                .filter(book -> book.getTotalCopies() > 0)
                .toList();

        List<Book> booksWithCategoryFilter  = booksWithCopies.stream()
                .filter(book -> category == null || book.getCategory().equalsIgnoreCase(category))
                .toList();

        List<Book> booksWithAvailableFilter = booksWithCategoryFilter.stream()
                .filter(book -> available == null || book.isAvailable() == available)
                .toList();

        List<BookResponse> bookResponseList = booksWithAvailableFilter.stream()
                .map(book -> modelMapper.map(book, BookResponse.class))
                .toList();

        BookData bookData = BookData.builder()
            .books(bookResponseList)
            .pageNumber(bookPage.getNumber())
            .pageSize(bookPage.getSize())
            .totalElements(bookPage.getTotalElements())
            .totalPages(bookPage.getTotalPages())
            .lastPage(bookPage.isLast())
            .build();

        return bookData;
    }

    @Override
    public BookResponse addBook(CreateBookRequest createBookRequest) {
        Book newBook = modelMapper.map(createBookRequest, Book.class);
        Book existingBook = bookRepository.findByTitleAndAuthor(
                newBook.getTitle(), newBook.getAuthor()
        ).orElse(null);

        if(existingBook != null){
            existingBook.addTotalCopies(newBook.getTotalCopies());
            existingBook.addAvailableCopies(newBook.getTotalCopies());
            existingBook.setAvailable(true);

            Book updatedBook = bookRepository.save(existingBook);
            return modelMapper.map(updatedBook, BookResponse.class);
        }

        newBook.setAvailableCopies(newBook.getTotalCopies());
        newBook.setAvailable(true);

        Book savedBook = bookRepository.save(newBook);
        return modelMapper.map(savedBook, BookResponse.class);
    }

    @Override
    public BookResponse updateBook(UpdateBookRequest updateBookRequest, UUID id) {
        Book savedBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        if(updateBookRequest.getTitle() != null){
            savedBook.setTitle(updateBookRequest.getTitle());
        }
        if(updateBookRequest.getCategory() != null){
            savedBook.setCategory(updateBookRequest.getCategory());
        }

        if (updateBookRequest.getAuthor() != null){
            savedBook.setAuthor(updateBookRequest.getAuthor());
        }

        if (updateBookRequest.getTotalCopies() != null && updateBookRequest.getTotalCopies() > 0 && updateBookRequest.getTotalCopies() > 0) {
            savedBook.addTotalCopies(updateBookRequest.getTotalCopies());
            savedBook.addAvailableCopies(updateBookRequest.getTotalCopies());
            savedBook.setAvailable(true);
        }

        Book updatedBook = bookRepository.save(savedBook);
        return modelMapper.map(updatedBook, BookResponse.class);
    }

    @Override
    public BookResponse removeBook(UUID id) {
        Book savedBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        savedBook.setTotalCopies(0);
        savedBook.setAvailableCopies(0);
        savedBook.setAvailable(false);

        Book removedBook = bookRepository.save(savedBook);
        return modelMapper.map(removedBook, BookResponse.class);
    }
}
