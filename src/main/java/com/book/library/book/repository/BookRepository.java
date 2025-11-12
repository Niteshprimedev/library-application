package com.book.library.book.repository;

import com.book.library.book.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    Book findByTitleAndAuthor(String title, String Author);
}
