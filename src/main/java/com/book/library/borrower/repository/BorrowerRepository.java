package com.book.library.borrower.repository;

import com.book.library.borrower.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {
    Optional<Borrower> findByEmail(String email);
}
