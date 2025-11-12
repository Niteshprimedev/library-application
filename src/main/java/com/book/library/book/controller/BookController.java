package com.book.library.book.controller;

import com.book.library.book.dto.BookData;
import com.book.library.book.dto.CreateBookRequest;
import com.book.library.book.dto.BookResponse;
import com.book.library.book.dto.UpdateBookRequest;
import com.book.library.book.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @GetMapping("")
    public ResponseEntity<BookData> getBooks(){
        BookData bookData = bookService.getBooks();
        return new ResponseEntity<>(bookData, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody CreateBookRequest createBookRequest){
        BookResponse bookResponse = bookService.addBook(createBookRequest);
        return new ResponseEntity<>(bookResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@RequestBody UpdateBookRequest updateBookRequest, @PathVariable UUID id){
        BookResponse bookResponse = bookService.updateBook(updateBookRequest, id);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookResponse> removeBook(@PathVariable UUID id){
        BookResponse bookResponse = bookService.removeBook(id);
        return new ResponseEntity<>(bookResponse, HttpStatus.OK);
    }
}
