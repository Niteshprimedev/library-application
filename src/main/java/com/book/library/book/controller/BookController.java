package com.book.library.book.controller;

import com.book.library.book.dto.BookData;
import com.book.library.book.dto.CreateBookRequest;
import com.book.library.book.dto.BookResponse;
import com.book.library.book.dto.UpdateBookRequest;
import com.book.library.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<BookData> getBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        BookData bookData = bookService.getBooks(category, available, page, size, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(bookData);
    }

    @PostMapping
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody CreateBookRequest createBookRequest){
        BookResponse bookResponse = bookService.addBook(createBookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@RequestBody UpdateBookRequest updateBookRequest, @PathVariable UUID id){
        BookResponse bookResponse = bookService.updateBook(updateBookRequest, id);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeBook(@PathVariable UUID id){
        bookService.removeBook(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
