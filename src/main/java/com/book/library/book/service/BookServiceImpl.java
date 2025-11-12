package com.book.library.book.service;

import com.book.library.book.dto.BookData;
import com.book.library.book.dto.CreateBookRequest;
import com.book.library.book.dto.BookResponse;
import com.book.library.book.dto.UpdateBookRequest;
import com.book.library.book.model.Book;
import com.book.library.book.repository.BookRepository;
import com.book.library.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BookServiceImpl implements BookService{

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper){
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookData getBooks() {
        List<BookResponse> books = bookRepository.findAll().stream()
                .filter(book -> book.getTotalCopies() > 0)
                .map(book -> modelMapper.map(book, BookResponse.class)).toList();

        BookData bookData = new BookData();
        bookData.setData(books);

        return bookData;
    }

    @Override
    public BookResponse addBook(CreateBookRequest createBookRequest) {
        Book newBook = modelMapper.map(createBookRequest, Book.class);
        Book existingBook = bookRepository.findByTitleAndAuthor(
                newBook.getTitle(), newBook.getAuthor()
        );

        if(existingBook != null){
            existingBook.updateTotalCopies(newBook.getTotalCopies());
            existingBook.updateAvailableCopies(newBook.getTotalCopies());
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

        savedBook.updateTotalCopies(updateBookRequest.getTotalCopies());
        savedBook.updateAvailableCopies(updateBookRequest.getTotalCopies());
        savedBook.setAvailable(true);

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
