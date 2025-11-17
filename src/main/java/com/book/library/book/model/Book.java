package com.book.library.book.model;

import com.book.library.borrowRecord.model.BorrowRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private boolean available = true;

    @Column(nullable = false)
    private int totalCopies;

    @Column(nullable = false)
    private int availableCopies;

    @JsonIgnore
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    public void addTotalCopies(int newCopies){
        this.totalCopies += newCopies;
    }

    public void addAvailableCopies(int newCopies){
        this.availableCopies += newCopies;
    }

    public void borrowOneCopy() {
        this.availableCopies--;
        this.available = this.availableCopies > 0;
    }

    public void returnOneCopy() {
        this.availableCopies++;
        this.available = true;
    }
}
