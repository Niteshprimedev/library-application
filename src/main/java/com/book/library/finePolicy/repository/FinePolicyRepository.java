package com.book.library.finePolicy.repository;

import com.book.library.finePolicy.model.FinePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinePolicyRepository extends JpaRepository<FinePolicy, UUID> {
    Optional<FinePolicy> findByCategory(String category);
}