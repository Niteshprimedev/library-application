package com.book.library.book.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {
    private UUID id;
    private String title;
    private String author;
    private String category;
    private boolean isAvailable;
    private int totalCopies;
    private int availableCopies;
}


